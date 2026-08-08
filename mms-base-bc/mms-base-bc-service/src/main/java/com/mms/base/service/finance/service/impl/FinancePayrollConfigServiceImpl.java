package com.mms.base.service.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mms.base.common.finance.dto.FinancePayrollConfigSaveDto;
import com.mms.base.common.finance.dto.FinancePayrollLineSaveDto;
import com.mms.base.common.finance.entity.FinanceAccountEntity;
import com.mms.base.common.finance.entity.FinanceCategoryEntity;
import com.mms.base.common.finance.entity.FinancePayrollLineEntity;
import com.mms.base.common.finance.entity.FinancePayrollProfileEntity;
import com.mms.base.common.finance.vo.FinancePayrollConfigVo;
import com.mms.base.common.finance.vo.FinancePayrollLineVo;
import com.mms.base.service.finance.mapper.FinanceAccountMapper;
import com.mms.base.service.finance.mapper.FinanceCategoryMapper;
import com.mms.base.service.finance.mapper.FinancePayrollLineMapper;
import com.mms.base.service.finance.mapper.FinancePayrollProfileMapper;
import com.mms.base.service.finance.service.FinancePayrollConfigService;
import com.mms.base.service.finance.support.FinanceUserSupport;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import com.mms.common.core.utils.IdUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 实现功能【工资录入配置服务实现】
 *
 * @author li.hongyu
 * @date 2026-08-01
 */
@Slf4j
@Service
public class FinancePayrollConfigServiceImpl implements FinancePayrollConfigService {

    private static final Set<String> LINE_TYPES = Set.of("income", "expense", "transfer");

    @Resource
    private FinancePayrollProfileMapper financePayrollProfileMapper;

    @Resource
    private FinancePayrollLineMapper financePayrollLineMapper;

    @Resource
    private FinanceAccountMapper financeAccountMapper;

    @Resource
    private FinanceCategoryMapper financeCategoryMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinancePayrollConfigVo getCurrent() {
        try {
            Long userId = FinanceUserSupport.requireUserId();
            FinancePayrollProfileEntity profile = getProfile(userId);
            if (profile == null) {
                profile = createDefaultProfile(userId);
            }
            return toVo(profile, listLines(profile.getId(), userId));
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询工资录入配置失败：{}", e.getMessage(), e);
            throw new ServerException("查询工资录入配置失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinancePayrollConfigVo save(FinancePayrollConfigSaveDto dto) {
        try {
            Long userId = FinanceUserSupport.requireUserId();
            validateSave(dto, userId);

            FinancePayrollProfileEntity profile = getProfile(userId);
            if (profile == null) {
                profile = new FinancePayrollProfileEntity();
                profile.setUserId(userId);
            }
            if (profile.getId() == null) {
                profile.setSalaryAccountId(dto.getSalaryAccountId());
                profile.setSalaryCategoryId(dto.getSalaryCategoryId());
                profile.setCompanyCardAccountId(null);
                profile.setMedicalAccountId(null);
                profile.setHousingFundAccountId(null);
                financePayrollProfileMapper.insert(profile);
            } else {
                // 显式 set null，避免 updateById 忽略空字段导致旧头账户残留
                financePayrollProfileMapper.update(
                        null,
                        new LambdaUpdateWrapper<FinancePayrollProfileEntity>()
                                .eq(FinancePayrollProfileEntity::getId, profile.getId())
                                .set(FinancePayrollProfileEntity::getSalaryAccountId, dto.getSalaryAccountId())
                                .set(FinancePayrollProfileEntity::getSalaryCategoryId, dto.getSalaryCategoryId())
                                .set(FinancePayrollProfileEntity::getCompanyCardAccountId, null)
                                .set(FinancePayrollProfileEntity::getMedicalAccountId, null)
                                .set(FinancePayrollProfileEntity::getHousingFundAccountId, null));
                profile.setSalaryAccountId(dto.getSalaryAccountId());
                profile.setSalaryCategoryId(dto.getSalaryCategoryId());
            }

            List<FinancePayrollLineEntity> existing = listLines(profile.getId(), userId);
            for (FinancePayrollLineEntity line : existing) {
                financePayrollLineMapper.deleteById(line.getId());
            }
            int sort = 10;
            Set<String> seenKeys = new HashSet<>();
            for (FinancePayrollLineSaveDto lineDto : dto.getLines()) {
                String lineKey = resolveLineKey(lineDto.getLineKey(), seenKeys);
                FinancePayrollLineEntity line = new FinancePayrollLineEntity();
                line.setUserId(userId);
                line.setProfileId(profile.getId());
                line.setLineKey(lineKey);
                line.setLabel(lineDto.getLabel().trim());
                line.setLineType(lineDto.getLineType().trim());
                if ("transfer".equals(line.getLineType())) {
                    line.setCategoryId(null);
                    line.setAccountId(null);
                    line.setFromAccountId(lineDto.getFromAccountId());
                    line.setToAccountId(lineDto.getToAccountId());
                } else {
                    line.setCategoryId(lineDto.getCategoryId());
                    line.setAccountId(lineDto.getAccountId());
                    line.setFromAccountId(null);
                    line.setToAccountId(null);
                }
                line.setCountInNet(lineDto.getCountInNet());
                line.setDefaultAmount(scaleMoney(lineDto.getDefaultAmount()));
                line.setSortOrder(lineDto.getSortOrder() == null ? sort : lineDto.getSortOrder());
                line.setEnabled(lineDto.getEnabled());
                financePayrollLineMapper.insert(line);
                sort += 10;
            }
            return toVo(profile, listLines(profile.getId(), userId));
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("保存工资录入配置失败：{}", e.getMessage(), e);
            throw new ServerException("保存工资录入配置失败", e);
        }
    }

    private void validateSave(FinancePayrollConfigSaveDto dto, Long userId) {
        ensureAccountOwned(dto.getSalaryAccountId(), userId, true);
        ensureCategoryOwned(dto.getSalaryCategoryId(), userId, "income", true);

        boolean hasEnabledIncome = false;
        Set<String> seen = new HashSet<>();
        for (FinancePayrollLineSaveDto line : dto.getLines()) {
            if (!LINE_TYPES.contains(line.getLineType())) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "明细行类型不合法：" + line.getLineType());
            }
            if (!Integer.valueOf(0).equals(line.getCountInNet()) && !Integer.valueOf(1).equals(line.getCountInNet())) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "countInNet 仅支持 0/1");
            }
            if (!Integer.valueOf(0).equals(line.getEnabled()) && !Integer.valueOf(1).equals(line.getEnabled())) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "enabled 仅支持 0/1");
            }
            if (StringUtils.hasText(line.getLineKey())) {
                String key = line.getLineKey().trim();
                if (!seen.add(key)) {
                    throw new BusinessException(ErrorCode.PARAM_INVALID, "明细行键重复：" + key);
                }
            }
            boolean enabled = Integer.valueOf(1).equals(line.getEnabled());
            if ("transfer".equals(line.getLineType())) {
                if (enabled) {
                    ensureAccountOwned(line.getFromAccountId(), userId, true);
                    ensureAccountOwned(line.getToAccountId(), userId, true);
                    if (Objects.equals(line.getFromAccountId(), line.getToAccountId())) {
                        throw new BusinessException(ErrorCode.PARAM_INVALID,
                                "转账行转出与转入不能相同：" + line.getLabel());
                    }
                } else {
                    ensureAccountOwned(line.getFromAccountId(), userId, false);
                    ensureAccountOwned(line.getToAccountId(), userId, false);
                }
            } else {
                if (enabled) {
                    ensureCategoryOwned(line.getCategoryId(), userId, line.getLineType(), true);
                    ensureAccountOwned(line.getAccountId(), userId, true);
                    if ("income".equals(line.getLineType())) {
                        hasEnabledIncome = true;
                    }
                } else {
                    ensureCategoryOwned(line.getCategoryId(), userId, line.getLineType(), false);
                    ensureAccountOwned(line.getAccountId(), userId, false);
                }
            }
        }
        if (!hasEnabledIncome) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "请至少保留一条启用的收入明细");
        }
    }

    private String resolveLineKey(String raw, Set<String> seenKeys) {
        String key = StringUtils.hasText(raw) ? raw.trim() : IdUtils.uuid32();
        if (key.length() > 32) {
            key = key.substring(0, 32);
        }
        while (!seenKeys.add(key)) {
            key = IdUtils.uuid32();
        }
        return key;
    }

    private FinancePayrollProfileEntity getProfile(Long userId) {
        return financePayrollProfileMapper.selectOne(
                new LambdaQueryWrapper<FinancePayrollProfileEntity>()
                        .eq(FinancePayrollProfileEntity::getUserId, userId)
                        .last("LIMIT 1"));
    }

    private List<FinancePayrollLineEntity> listLines(Long profileId, Long userId) {
        return financePayrollLineMapper.selectList(
                new LambdaQueryWrapper<FinancePayrollLineEntity>()
                        .eq(FinancePayrollLineEntity::getProfileId, profileId)
                        .eq(FinancePayrollLineEntity::getUserId, userId)
                        .orderByAsc(FinancePayrollLineEntity::getSortOrder)
                        .orderByAsc(FinancePayrollLineEntity::getId));
    }

    private FinancePayrollProfileEntity createDefaultProfile(Long userId) {
        Map<String, Long> accountByName = listAccounts(userId).stream()
                .collect(Collectors.toMap(FinanceAccountEntity::getName, FinanceAccountEntity::getId, (a, b) -> a));
        Map<String, Long> incomeCat = listCategories(userId, "income").stream()
                .collect(Collectors.toMap(FinanceCategoryEntity::getName, FinanceCategoryEntity::getId, (a, b) -> a));
        Map<String, Long> expenseCat = listCategories(userId, "expense").stream()
                .collect(Collectors.toMap(FinanceCategoryEntity::getName, FinanceCategoryEntity::getId, (a, b) -> a));

        Long salaryAccountId = firstNonNull(accountByName, "招商卡", "银行卡", "支付宝", "微信");
        Long companyCardId = accountByName.get("公司卡");
        Long medicalId = firstNonNull(accountByName, "医保卡", "医保");
        Long housingId = accountByName.get("公积金");
        Long salaryCategoryId = incomeCat.get("工资");

        FinancePayrollProfileEntity profile = new FinancePayrollProfileEntity();
        profile.setUserId(userId);
        profile.setSalaryAccountId(salaryAccountId);
        profile.setSalaryCategoryId(salaryCategoryId);
        profile.setCompanyCardAccountId(null);
        profile.setMedicalAccountId(null);
        profile.setHousingFundAccountId(null);
        financePayrollProfileMapper.insert(profile);

        List<DefaultLine> defaults = defaultLines(
                salaryAccountId, companyCardId, medicalId, housingId,
                salaryCategoryId, incomeCat, expenseCat);
        int sort = 10;
        for (DefaultLine def : defaults) {
            FinancePayrollLineEntity line = new FinancePayrollLineEntity();
            line.setUserId(userId);
            line.setProfileId(profile.getId());
            line.setLineKey(def.lineKey);
            line.setLabel(def.label);
            line.setLineType(def.lineType);
            line.setCategoryId(def.categoryId);
            line.setAccountId(def.accountId);
            line.setFromAccountId(def.fromAccountId);
            line.setToAccountId(def.toAccountId);
            line.setCountInNet(def.countInNet);
            line.setDefaultAmount(def.defaultAmount);
            line.setSortOrder(sort);
            line.setEnabled(def.enabled);
            financePayrollLineMapper.insert(line);
            sort += 10;
        }
        return profile;
    }

    private List<DefaultLine> defaultLines(Long salaryAccountId, Long companyCardId, Long medicalId, Long housingId,
                                           Long salaryCategoryId, Map<String, Long> incomeCat,
                                           Map<String, Long> expenseCat) {
        List<DefaultLine> list = new ArrayList<>();
        list.add(income("base_salary", "基本工资", salaryCategoryId, salaryAccountId, true, "7200",
                readyIncome(salaryCategoryId, salaryAccountId)));
        list.add(income("computer_subsidy", "电脑补贴", incomeCat.get("电脑补贴"), salaryAccountId, true, "100",
                readyIncome(incomeCat.get("电脑补贴"), salaryAccountId)));
        list.add(income("overtime", "加班/绩效", incomeCat.get("加班费"), salaryAccountId, true, "0",
                readyIncome(incomeCat.get("加班费"), salaryAccountId)));
        list.add(income("meal_allowance", "餐补", incomeCat.get("餐补"), companyCardId, false, "0",
                readyIncome(incomeCat.get("餐补"), companyCardId)));
        list.add(transfer("personal_medical", "个人医保", salaryAccountId, medicalId, true, "100",
                readyTransfer(salaryAccountId, medicalId)));
        list.add(expense("social_other", "社保其他", expenseCat.get("社保其他"), salaryAccountId, true, "425",
                readyIncome(expenseCat.get("社保其他"), salaryAccountId)));
        list.add(transfer("personal_housing_fund", "个人公积金", salaryAccountId, housingId, true, "300",
                readyTransfer(salaryAccountId, housingId)));
        list.add(income("company_housing_fund", "公司公积金", incomeCat.get("公司公积金"), housingId, false, "300",
                readyIncome(incomeCat.get("公司公积金"), housingId)));
        list.add(expense("tax", "个税", expenseCat.get("个税"), salaryAccountId, true, "0",
                readyIncome(expenseCat.get("个税"), salaryAccountId)));
        return list;
    }

    private boolean readyIncome(Long categoryId, Long accountId) {
        return categoryId != null && accountId != null;
    }

    private boolean readyTransfer(Long fromAccountId, Long toAccountId) {
        return fromAccountId != null && toAccountId != null && !Objects.equals(fromAccountId, toAccountId);
    }

    private DefaultLine income(String key, String label, Long categoryId, Long accountId,
                               boolean countInNet, String amount, boolean ready) {
        return new DefaultLine(key, label, "income", categoryId, accountId, null, null,
                countInNet ? 1 : 0, new BigDecimal(amount), ready ? 1 : 0);
    }

    private DefaultLine expense(String key, String label, Long categoryId, Long accountId,
                                boolean countInNet, String amount, boolean ready) {
        return new DefaultLine(key, label, "expense", categoryId, accountId, null, null,
                countInNet ? 1 : 0, new BigDecimal(amount), ready ? 1 : 0);
    }

    private DefaultLine transfer(String key, String label, Long from, Long to,
                                 boolean countInNet, String amount, boolean ready) {
        return new DefaultLine(key, label, "transfer", null, null, from, to,
                countInNet ? 1 : 0, new BigDecimal(amount), ready ? 1 : 0);
    }

    private FinancePayrollConfigVo toVo(FinancePayrollProfileEntity profile, List<FinancePayrollLineEntity> lines) {
        Map<Long, FinanceAccountEntity> accountMap = listAccounts(profile.getUserId()).stream()
                .collect(Collectors.toMap(FinanceAccountEntity::getId, Function.identity(), (a, b) -> a));
        Map<Long, FinanceCategoryEntity> categoryMap = new HashMap<>();
        listCategories(profile.getUserId(), null).forEach(c -> categoryMap.put(c.getId(), c));

        FinancePayrollConfigVo vo = new FinancePayrollConfigVo();
        vo.setId(profile.getId());
        vo.setSalaryAccountId(profile.getSalaryAccountId());
        vo.setSalaryAccountName(nameOf(accountMap, profile.getSalaryAccountId()));
        vo.setSalaryCategoryId(profile.getSalaryCategoryId());
        FinanceCategoryEntity salaryCat = categoryMap.get(profile.getSalaryCategoryId());
        vo.setSalaryCategoryName(salaryCat == null ? null : salaryCat.getName());

        List<FinancePayrollLineVo> lineVos = new ArrayList<>();
        for (FinancePayrollLineEntity line : lines) {
            FinancePayrollLineVo item = new FinancePayrollLineVo();
            item.setId(line.getId());
            item.setLineKey(line.getLineKey());
            item.setLabel(line.getLabel());
            item.setLineType(line.getLineType());
            item.setCategoryId(line.getCategoryId());
            FinanceCategoryEntity cat = categoryMap.get(line.getCategoryId());
            item.setCategoryName(cat == null ? null : cat.getName());
            item.setAccountId(line.getAccountId());
            item.setAccountName(nameOf(accountMap, line.getAccountId()));
            item.setFromAccountId(line.getFromAccountId());
            item.setFromAccountName(nameOf(accountMap, line.getFromAccountId()));
            item.setToAccountId(line.getToAccountId());
            item.setToAccountName(nameOf(accountMap, line.getToAccountId()));
            item.setCountInNet(line.getCountInNet());
            item.setDefaultAmount(line.getDefaultAmount());
            item.setSortOrder(line.getSortOrder());
            item.setEnabled(line.getEnabled());
            lineVos.add(item);
        }
        vo.setLines(lineVos);
        return vo;
    }

    private List<FinanceAccountEntity> listAccounts(Long userId) {
        return financeAccountMapper.selectList(
                new LambdaQueryWrapper<FinanceAccountEntity>()
                        .eq(FinanceAccountEntity::getUserId, userId)
                        .orderByAsc(FinanceAccountEntity::getSortOrder));
    }

    private List<FinanceCategoryEntity> listCategories(Long userId, String direction) {
        LambdaQueryWrapper<FinanceCategoryEntity> wrapper = new LambdaQueryWrapper<FinanceCategoryEntity>()
                .eq(FinanceCategoryEntity::getUserId, userId);
        if (StringUtils.hasText(direction)) {
            wrapper.eq(FinanceCategoryEntity::getDirection, direction);
        }
        return financeCategoryMapper.selectList(wrapper.orderByAsc(FinanceCategoryEntity::getSortOrder));
    }

    private void ensureAccountOwned(Long accountId, Long userId, boolean required) {
        if (accountId == null) {
            if (required) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "账户不能为空");
            }
            return;
        }
        FinanceAccountEntity account = financeAccountMapper.selectById(accountId);
        if (account == null || !userId.equals(account.getUserId())) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "账户不存在");
        }
    }

    private void ensureCategoryOwned(Long categoryId, Long userId, String expectedDirection, boolean required) {
        if (categoryId == null) {
            if (required) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "分类不能为空");
            }
            return;
        }
        FinanceCategoryEntity category = financeCategoryMapper.selectById(categoryId);
        if (category == null || !userId.equals(category.getUserId())) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "分类不存在");
        }
        if (StringUtils.hasText(expectedDirection) && !expectedDirection.equals(category.getDirection())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "分类方向与行类型不一致");
        }
    }

    private String nameOf(Map<Long, FinanceAccountEntity> map, Long id) {
        if (id == null) {
            return null;
        }
        FinanceAccountEntity entity = map.get(id);
        return entity == null ? null : entity.getName();
    }

    private Long firstNonNull(Map<String, Long> map, String... names) {
        for (String name : names) {
            Long id = map.get(name);
            if (id != null) {
                return id;
            }
        }
        return null;
    }

    private BigDecimal scaleMoney(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private record DefaultLine(String lineKey, String label, String lineType, Long categoryId, Long accountId,
                               Long fromAccountId, Long toAccountId, Integer countInNet, BigDecimal defaultAmount,
                               Integer enabled) {
    }
}
