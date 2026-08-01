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
    private static final Set<String> LINE_KEYS = Set.of(
            "base_salary", "computer_subsidy", "overtime", "meal_allowance",
            "personal_medical", "social_other", "personal_housing_fund",
            "company_housing_fund", "tax");

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
            normalizeLineAccounts(dto);

            FinancePayrollProfileEntity profile = getProfile(userId);
            if (profile == null) {
                profile = new FinancePayrollProfileEntity();
                profile.setUserId(userId);
            }
            profile.setSalaryAccountId(dto.getSalaryAccountId());
            profile.setCompanyCardAccountId(dto.getCompanyCardAccountId());
            profile.setMedicalAccountId(dto.getMedicalAccountId());
            profile.setHousingFundAccountId(dto.getHousingFundAccountId());
            profile.setSalaryCategoryId(dto.getSalaryCategoryId());
            if (profile.getId() == null) {
                financePayrollProfileMapper.insert(profile);
            } else {
                financePayrollProfileMapper.updateById(profile);
            }

            List<FinancePayrollLineEntity> existing = listLines(profile.getId(), userId);
            for (FinancePayrollLineEntity line : existing) {
                financePayrollLineMapper.deleteById(line.getId());
            }
            int sort = 10;
            for (FinancePayrollLineSaveDto lineDto : dto.getLines()) {
                FinancePayrollLineEntity line = new FinancePayrollLineEntity();
                line.setUserId(userId);
                line.setProfileId(profile.getId());
                line.setLineKey(lineDto.getLineKey().trim());
                line.setLabel(lineDto.getLabel().trim());
                line.setLineType(lineDto.getLineType().trim());
                line.setCategoryId(lineDto.getCategoryId());
                line.setAccountId(lineDto.getAccountId());
                line.setFromAccountId(lineDto.getFromAccountId());
                line.setToAccountId(lineDto.getToAccountId());
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
        ensureAccountOwned(dto.getCompanyCardAccountId(), userId, false);
        ensureAccountOwned(dto.getMedicalAccountId(), userId, false);
        ensureAccountOwned(dto.getHousingFundAccountId(), userId, false);
        ensureCategoryOwned(dto.getSalaryCategoryId(), userId, "income", true);

        Set<String> seen = new HashSet<>();
        for (FinancePayrollLineSaveDto line : dto.getLines()) {
            if (!LINE_KEYS.contains(line.getLineKey())) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "不支持的明细行键：" + line.getLineKey());
            }
            if (!seen.add(line.getLineKey())) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "明细行键重复：" + line.getLineKey());
            }
            if (!LINE_TYPES.contains(line.getLineType())) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "明细行类型不合法：" + line.getLineType());
            }
            if (!Integer.valueOf(0).equals(line.getCountInNet()) && !Integer.valueOf(1).equals(line.getCountInNet())) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "countInNet 仅支持 0/1");
            }
            if (!Integer.valueOf(0).equals(line.getEnabled()) && !Integer.valueOf(1).equals(line.getEnabled())) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "enabled 仅支持 0/1");
            }
            if ("transfer".equals(line.getLineType())) {
                ensureAccountOwned(line.getFromAccountId(), userId, true);
                ensureAccountOwned(line.getToAccountId(), userId, true);
                if (Objects.equals(line.getFromAccountId(), line.getToAccountId())) {
                    throw new BusinessException(ErrorCode.PARAM_INVALID, "转账行转出与转入不能相同：" + line.getLabel());
                }
            } else {
                ensureCategoryOwned(line.getCategoryId(), userId, line.getLineType(), true);
                ensureAccountOwned(line.getAccountId(), userId, true);
            }
        }
        if (!seen.contains("base_salary")) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "必须包含基本工资行（base_salary）");
        }
    }

    /**
     * 入账接口仍按配置头账户拆流水，保存时把明细行账户对齐到头，避免前后端理解不一致。
     */
    private void normalizeLineAccounts(FinancePayrollConfigSaveDto dto) {
        for (FinancePayrollLineSaveDto line : dto.getLines()) {
            switch (line.getLineKey()) {
                case "base_salary", "computer_subsidy", "overtime", "social_other", "tax" -> {
                    line.setAccountId(dto.getSalaryAccountId());
                    line.setFromAccountId(null);
                    line.setToAccountId(null);
                }
                case "meal_allowance" -> {
                    line.setAccountId(dto.getCompanyCardAccountId());
                    line.setFromAccountId(null);
                    line.setToAccountId(null);
                }
                case "company_housing_fund" -> {
                    line.setAccountId(dto.getHousingFundAccountId());
                    line.setFromAccountId(null);
                    line.setToAccountId(null);
                }
                case "personal_medical" -> {
                    line.setAccountId(null);
                    line.setCategoryId(null);
                    line.setFromAccountId(dto.getSalaryAccountId());
                    line.setToAccountId(dto.getMedicalAccountId());
                }
                case "personal_housing_fund" -> {
                    line.setAccountId(null);
                    line.setCategoryId(null);
                    line.setFromAccountId(dto.getSalaryAccountId());
                    line.setToAccountId(dto.getHousingFundAccountId());
                }
                default -> {
                }
            }
        }
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
        profile.setCompanyCardAccountId(companyCardId);
        profile.setMedicalAccountId(medicalId);
        profile.setHousingFundAccountId(housingId);
        profile.setSalaryCategoryId(salaryCategoryId);
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
            line.setEnabled(1);
            financePayrollLineMapper.insert(line);
            sort += 10;
        }
        return profile;
    }

    private List<DefaultLine> defaultLines(Long salaryAccountId, Long companyCardId, Long medicalId, Long housingId,
                                           Long salaryCategoryId, Map<String, Long> incomeCat,
                                           Map<String, Long> expenseCat) {
        List<DefaultLine> list = new ArrayList<>();
        list.add(income("base_salary", "基本工资", salaryCategoryId, salaryAccountId, true, "7200"));
        list.add(income("computer_subsidy", "电脑补贴", incomeCat.get("电脑补贴"), salaryAccountId, true, "100"));
        list.add(income("overtime", "加班/绩效", incomeCat.get("加班费"), salaryAccountId, true, "0"));
        list.add(income("meal_allowance", "餐补", incomeCat.get("餐补"), companyCardId, false, "0"));
        list.add(transfer("personal_medical", "个人医保", salaryAccountId, medicalId, true, "100"));
        list.add(expense("social_other", "社保其他", expenseCat.get("社保其他"), salaryAccountId, true, "425"));
        list.add(transfer("personal_housing_fund", "个人公积金", salaryAccountId, housingId, true, "300"));
        list.add(income("company_housing_fund", "公司公积金", incomeCat.get("公司公积金"), housingId, false, "300"));
        list.add(expense("tax", "个税", expenseCat.get("个税"), salaryAccountId, true, "0"));
        return list;
    }

    private DefaultLine income(String key, String label, Long categoryId, Long accountId,
                               boolean countInNet, String amount) {
        return new DefaultLine(key, label, "income", categoryId, accountId, null, null,
                countInNet ? 1 : 0, new BigDecimal(amount));
    }

    private DefaultLine expense(String key, String label, Long categoryId, Long accountId,
                                boolean countInNet, String amount) {
        return new DefaultLine(key, label, "expense", categoryId, accountId, null, null,
                countInNet ? 1 : 0, new BigDecimal(amount));
    }

    private DefaultLine transfer(String key, String label, Long from, Long to,
                                 boolean countInNet, String amount) {
        return new DefaultLine(key, label, "transfer", null, null, from, to,
                countInNet ? 1 : 0, new BigDecimal(amount));
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
        vo.setCompanyCardAccountId(profile.getCompanyCardAccountId());
        vo.setCompanyCardAccountName(nameOf(accountMap, profile.getCompanyCardAccountId()));
        vo.setMedicalAccountId(profile.getMedicalAccountId());
        vo.setMedicalAccountName(nameOf(accountMap, profile.getMedicalAccountId()));
        vo.setHousingFundAccountId(profile.getHousingFundAccountId());
        vo.setHousingFundAccountName(nameOf(accountMap, profile.getHousingFundAccountId()));
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
                               Long fromAccountId, Long toAccountId, Integer countInNet, BigDecimal defaultAmount) {
    }
}
