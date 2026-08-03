package com.mms.base.service.finance.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.finance.dto.FinanceAdjustmentDto;
import com.mms.base.common.finance.dto.FinancePayrollBatchDto;
import com.mms.base.common.finance.dto.FinanceTransactionBatchDeleteDto;
import com.mms.base.common.finance.dto.FinanceTransactionCreateDto;
import com.mms.base.common.finance.dto.FinanceTransactionFromRecurringDto;
import com.mms.base.common.finance.dto.FinanceTransactionPageQueryDto;
import com.mms.base.common.finance.dto.FinanceTransactionUpdateDto;
import com.mms.base.common.finance.entity.FinanceAccountEntity;
import com.mms.base.common.finance.entity.FinanceCategoryEntity;
import com.mms.base.common.finance.entity.FinanceRecurringEntity;
import com.mms.base.common.finance.entity.FinanceTransactionEntity;
import com.mms.base.common.finance.vo.FinanceAccountVo;
import com.mms.base.common.finance.vo.FinanceTransactionVo;
import com.mms.base.service.finance.mapper.FinanceAccountMapper;
import com.mms.base.service.finance.mapper.FinanceCategoryMapper;
import com.mms.base.service.finance.mapper.FinanceRecurringMapper;
import com.mms.base.service.finance.mapper.FinanceTransactionMapper;
import com.mms.base.service.finance.service.FinanceTransactionService;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 实现功能【记账流水服务实现类】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Slf4j
@Service
public class FinanceTransactionServiceImpl implements FinanceTransactionService {

    private static final Set<String> TXN_TYPES = Set.of("income", "expense", "transfer", "adjustment");
    private static final Set<String> STATUSES = Set.of("settled", "pending");

    @Resource
    private FinanceTransactionMapper financeTransactionMapper;

    @Resource
    private FinanceAccountMapper financeAccountMapper;

    @Resource
    private FinanceCategoryMapper financeCategoryMapper;

    @Resource
    private FinanceRecurringMapper financeRecurringMapper;

    @Override
    public Page<FinanceTransactionVo> getTransactionPage(FinanceTransactionPageQueryDto dto) {
        try {
            Long userId = FinanceUserSupport.requireUserId();
            log.info("分页查询记账流水，userId={}，参数：{}", userId, dto);
            Page<FinanceTransactionVo> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            return financeTransactionMapper.getTransactionPage(page, dto, userId);
        } catch (Exception e) {
            log.error("分页查询记账流水失败：{}", e.getMessage(), e);
            throw new ServerException("查询记账流水列表失败", e);
        }
    }

    @Override
    public FinanceTransactionVo getById(Long id) {
        try {
            Long userId = FinanceUserSupport.requireUserId();
            if (id == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "流水ID不能为空");
            }
            FinanceTransactionVo vo = financeTransactionMapper.getTransactionById(id, userId);
            if (vo == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "流水不存在");
            }
            return vo;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询记账流水详情失败：{}", e.getMessage(), e);
            throw new ServerException("查询记账流水详情失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceTransactionVo create(FinanceTransactionCreateDto dto) {
        try {
            Long userId = FinanceUserSupport.requireUserId();
            log.info("创建记账流水，userId={}，参数：{}", userId, dto);
            if ("adjustment".equals(dto.getTxnType())) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "平账请使用专用接口 /finance/transaction/adjust");
            }
            String status = StringUtils.hasText(dto.getStatus()) ? dto.getStatus() : "settled";
            validateTxnFields(dto.getTxnType(), dto.getAccountId(), dto.getCategoryId(),
                    dto.getFromAccountId(), dto.getToAccountId(), status);
            ensureAccountExists(dto.getAccountId(), userId, true);
            ensureAccountExists(dto.getFromAccountId(), userId, true);
            ensureAccountExists(dto.getToAccountId(), userId, true);
            ensureCategoryExists(dto.getCategoryId(), userId, true, dto.getTxnType());

            FinanceTransactionEntity entity = new FinanceTransactionEntity();
            entity.setUserId(userId);
            entity.setTxnDate(dto.getTxnDate());
            entity.setTxnType(dto.getTxnType());
            entity.setAmount(scaleMoney(dto.getAmount()));
            entity.setCategoryId(dto.getCategoryId());
            entity.setAccountId(dto.getAccountId());
            entity.setFromAccountId(dto.getFromAccountId());
            entity.setToAccountId(dto.getToAccountId());
            entity.setStatus(status);
            entity.setNote(dto.getNote());
            normalizeByTxnType(entity);
            financeTransactionMapper.insert(entity);
            return financeTransactionMapper.getTransactionById(entity.getId(), userId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建记账流水失败：{}", e.getMessage(), e);
            throw new ServerException("创建记账流水失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceTransactionVo update(FinanceTransactionUpdateDto dto) {
        try {
            Long userId = FinanceUserSupport.requireUserId();
            log.info("更新记账流水，userId={}，参数：{}", userId, dto);
            FinanceTransactionEntity entity = financeTransactionMapper.selectById(dto.getId());
            if (entity == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "流水不存在");
            }
            FinanceUserSupport.requireOwned(entity.getUserId(), "流水不存在");
            if (StringUtils.hasText(dto.getTxnType())
                    && !dto.getTxnType().equals(entity.getTxnType())) {
                if ("adjustment".equals(dto.getTxnType()) || "adjustment".equals(entity.getTxnType())) {
                    throw new BusinessException(ErrorCode.PARAM_INVALID,
                            "平账流水请使用专用平账接口创建，不能通过更新改成或改出平账类型");
                }
            }
            if (dto.getTxnDate() != null) {
                entity.setTxnDate(dto.getTxnDate());
            }
            if (StringUtils.hasText(dto.getTxnType())) {
                entity.setTxnType(dto.getTxnType());
            }
            if (dto.getAmount() != null) {
                entity.setAmount(scaleMoney(dto.getAmount()));
            }
            if (dto.getCategoryId() != null) {
                entity.setCategoryId(dto.getCategoryId());
            }
            if (dto.getAccountId() != null) {
                entity.setAccountId(dto.getAccountId());
            }
            if (dto.getFromAccountId() != null) {
                entity.setFromAccountId(dto.getFromAccountId());
            }
            if (dto.getToAccountId() != null) {
                entity.setToAccountId(dto.getToAccountId());
            }
            if (StringUtils.hasText(dto.getStatus())) {
                entity.setStatus(dto.getStatus());
            }
            if (dto.getNote() != null) {
                entity.setNote(dto.getNote());
            }
            if (!"adjustment".equals(entity.getTxnType())
                    && entity.getAmount() != null
                    && entity.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "金额必须大于0");
            }
            if ("adjustment".equals(entity.getTxnType())
                    && entity.getAmount() != null
                    && entity.getAmount().compareTo(BigDecimal.ZERO) == 0) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "平账差额不能为0");
            }
            validateTxnFields(entity.getTxnType(), entity.getAccountId(), entity.getCategoryId(),
                    entity.getFromAccountId(), entity.getToAccountId(), entity.getStatus());
            // 仅当本次显式改了账户/分类时要求仍启用；历史流水改状态/备注可沿用已禁用项
            ensureAccountExists(entity.getAccountId(), userId, dto.getAccountId() != null);
            ensureAccountExists(entity.getFromAccountId(), userId, dto.getFromAccountId() != null);
            ensureAccountExists(entity.getToAccountId(), userId, dto.getToAccountId() != null);
            ensureCategoryExists(entity.getCategoryId(), userId, dto.getCategoryId() != null, entity.getTxnType());
            normalizeByTxnType(entity);
            financeTransactionMapper.updateById(entity);
            return financeTransactionMapper.getTransactionById(entity.getId(), userId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新记账流水失败：{}", e.getMessage(), e);
            throw new ServerException("更新记账流水失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        try {
            if (id == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "流水ID不能为空");
            }
            FinanceTransactionEntity entity = financeTransactionMapper.selectById(id);
            if (entity == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "流水不存在");
            }
            FinanceUserSupport.requireOwned(entity.getUserId(), "流水不存在");
            financeTransactionMapper.deleteById(id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除记账流水失败：{}", e.getMessage(), e);
            throw new ServerException("删除记账流水失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(FinanceTransactionBatchDeleteDto dto) {
        try {
            if (dto.getIds() == null || dto.getIds().isEmpty()) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "流水ID列表不能为空");
            }
            for (Long id : dto.getIds()) {
                delete(id);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量删除记账流水失败：{}", e.getMessage(), e);
            throw new ServerException("批量删除记账流水失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceTransactionVo createFromRecurring(FinanceTransactionFromRecurringDto dto) {
        try {
            log.info("由快捷模板生成流水，参数：{}", dto);
            FinanceRecurringEntity recurring = financeRecurringMapper.selectById(dto.getRecurringId());
            if (recurring == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "快捷模板不存在");
            }
            FinanceUserSupport.requireOwned(recurring.getUserId(), "快捷模板不存在");
            if (!Integer.valueOf(1).equals(recurring.getEnabled())) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "快捷模板已禁用，无法生成流水");
            }
            BigDecimal amount = dto.getAmount() != null ? dto.getAmount() : recurring.getAmount();
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "金额必须大于0");
            }
            FinanceTransactionCreateDto createDto = new FinanceTransactionCreateDto();
            createDto.setTxnDate(dto.getTxnDate() != null ? dto.getTxnDate() : LocalDate.now());
            createDto.setTxnType(recurring.getDirection());
            createDto.setAmount(amount);
            createDto.setCategoryId(recurring.getCategoryId());
            createDto.setAccountId(recurring.getAccountId());
            createDto.setFromAccountId(recurring.getFromAccountId());
            createDto.setToAccountId(recurring.getToAccountId());
            createDto.setStatus("settled");
            createDto.setNote(StringUtils.hasText(dto.getNote()) ? dto.getNote() : recurring.getNote());
            return create(createDto);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("由快捷模板生成流水失败：{}", e.getMessage(), e);
            throw new ServerException("由快捷模板生成流水失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<FinanceTransactionVo> createPayrollBatch(FinancePayrollBatchDto dto) {
        try {
            log.info("工资条批量入账，参数：{}", dto);
            if (dto.getVoidTxnId() != null) {
                delete(dto.getVoidTxnId());
            }
            String mode = dto.getMode();
            if ("net_only".equals(mode)) {
                return List.of(createNetOnlyPayroll(dto));
            }
            if ("detail".equals(mode)) {
                return createDetailPayroll(dto);
            }
            throw new BusinessException(ErrorCode.PARAM_INVALID, "录入模式不合法，仅支持 net_only/detail");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("工资条批量入账失败：{}", e.getMessage(), e);
            throw new ServerException("工资条批量入账失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceTransactionVo createAdjustment(FinanceAdjustmentDto dto) {
        try {
            Long userId = FinanceUserSupport.requireUserId();
            log.info("平账，userId={}，参数：{}", userId, dto);
            FinanceAccountVo account = financeAccountMapper.getAccountWithBalance(dto.getAccountId(), userId);
            if (account == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "账户不存在");
            }
            if (!Integer.valueOf(1).equals(account.getEnabled())) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "账户已禁用：" + account.getName());
            }
            BigDecimal bookBalance = account.getBalance() == null ? BigDecimal.ZERO : account.getBalance();
            BigDecimal actual = scaleMoney(dto.getActualBalance());
            BigDecimal diff = actual.subtract(bookBalance).setScale(2, RoundingMode.HALF_UP);
            if (diff.compareTo(BigDecimal.ZERO) == 0) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "真实余额与账面一致，无需平账");
            }

            FinanceTransactionEntity entity = new FinanceTransactionEntity();
            entity.setUserId(userId);
            entity.setTxnDate(dto.getTxnDate());
            entity.setTxnType("adjustment");
            entity.setAmount(diff);
            entity.setAccountId(dto.getAccountId());
            entity.setCategoryId(null);
            entity.setFromAccountId(null);
            entity.setToAccountId(null);
            entity.setStatus("settled");
            String note = StringUtils.hasText(dto.getNote())
                    ? dto.getNote()
                    : String.format("平账：账面 %s → 真实 %s", bookBalance.toPlainString(), actual.toPlainString());
            entity.setNote(note);
            financeTransactionMapper.insert(entity);
            return financeTransactionMapper.getTransactionById(entity.getId(), userId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("平账失败：{}", e.getMessage(), e);
            throw new ServerException("平账失败", e);
        }
    }

    private FinanceTransactionVo createNetOnlyPayroll(FinancePayrollBatchDto dto) {
        if (dto.getNetAmount() == null || dto.getNetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "先记到手金额必须大于0");
        }
        FinanceTransactionCreateDto createDto = new FinanceTransactionCreateDto();
        createDto.setTxnDate(dto.getTxnDate());
        createDto.setTxnType("income");
        createDto.setAmount(dto.getNetAmount());
        createDto.setAccountId(dto.getSalaryAccountId());
        createDto.setCategoryId(dto.getSalaryCategoryId());
        createDto.setStatus("pending");
        createDto.setNote(StringUtils.hasText(dto.getNote()) ? dto.getNote() : "先记到手，待拆工资条");
        return create(createDto);
    }

    private List<FinanceTransactionVo> createDetailPayroll(FinancePayrollBatchDto dto) {
        if (dto.getCompanyCardAccountId() == null || dto.getMedicalAccountId() == null
                || dto.getHousingFundAccountId() == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "明细入账须指定公司卡、医保卡与公积金账户");
        }
        List<FinanceTransactionVo> created = new ArrayList<>();
        String notePrefix = StringUtils.hasText(dto.getNote()) ? dto.getNote() + " | " : "";

        addIncomeIfPositive(created, dto, dto.getBaseSalary(), dto.getSalaryAccountId(),
                dto.getSalaryCategoryId(), notePrefix + "基本工资");
        addIncomeIfPositive(created, dto, dto.getComputerSubsidy(), dto.getSalaryAccountId(),
                dto.getComputerSubsidyCategoryId(), notePrefix + "电脑补贴");
        addIncomeIfPositive(created, dto, dto.getOvertime(), dto.getSalaryAccountId(),
                dto.getOvertimeCategoryId(), notePrefix + "加班/绩效");
        addIncomeIfPositive(created, dto, dto.getMealAllowance(), dto.getCompanyCardAccountId(),
                dto.getMealAllowanceCategoryId(), notePrefix + "餐补");

        addTransferIfPositive(created, dto, dto.getPersonalMedical(),
                dto.getSalaryAccountId(), dto.getMedicalAccountId(), notePrefix + "个人医保");
        addExpenseIfPositive(created, dto, dto.getSocialOther(), dto.getSalaryAccountId(),
                dto.getSocialOtherCategoryId(), notePrefix + "社保其他");
        addTransferIfPositive(created, dto, dto.getPersonalHousingFund(),
                dto.getSalaryAccountId(), dto.getHousingFundAccountId(), notePrefix + "个人公积金");
        addIncomeIfPositive(created, dto, dto.getCompanyHousingFund(), dto.getHousingFundAccountId(),
                dto.getCompanyHousingFundCategoryId(), notePrefix + "公司公积金");
        // 公司医保为统筹缴费，不入个人医保卡、不生成流水
        addExpenseIfPositive(created, dto, dto.getTax(), dto.getSalaryAccountId(),
                dto.getTaxCategoryId(), notePrefix + "个税");

        if (created.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "没有可入账的金额项");
        }
        return created;
    }

    private void addIncomeIfPositive(List<FinanceTransactionVo> created, FinancePayrollBatchDto dto,
                                     BigDecimal amount, Long accountId, Long categoryId, String note) {
        if (!isPositive(amount)) {
            return;
        }
        if (categoryId == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, note + "分类不能为空");
        }
        FinanceTransactionCreateDto createDto = new FinanceTransactionCreateDto();
        createDto.setTxnDate(dto.getTxnDate());
        createDto.setTxnType("income");
        createDto.setAmount(amount);
        createDto.setAccountId(accountId);
        createDto.setCategoryId(categoryId);
        createDto.setStatus("settled");
        createDto.setNote(note);
        created.add(create(createDto));
    }

    private void addExpenseIfPositive(List<FinanceTransactionVo> created, FinancePayrollBatchDto dto,
                                      BigDecimal amount, Long accountId, Long categoryId, String note) {
        if (!isPositive(amount)) {
            return;
        }
        if (categoryId == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, note + "分类不能为空");
        }
        FinanceTransactionCreateDto createDto = new FinanceTransactionCreateDto();
        createDto.setTxnDate(dto.getTxnDate());
        createDto.setTxnType("expense");
        createDto.setAmount(amount);
        createDto.setAccountId(accountId);
        createDto.setCategoryId(categoryId);
        createDto.setStatus("settled");
        createDto.setNote(note);
        created.add(create(createDto));
    }

    private void addTransferIfPositive(List<FinanceTransactionVo> created, FinancePayrollBatchDto dto,
                                       BigDecimal amount, Long fromAccountId, Long toAccountId, String note) {
        if (!isPositive(amount)) {
            return;
        }
        FinanceTransactionCreateDto createDto = new FinanceTransactionCreateDto();
        createDto.setTxnDate(dto.getTxnDate());
        createDto.setTxnType("transfer");
        createDto.setAmount(amount);
        createDto.setFromAccountId(fromAccountId);
        createDto.setToAccountId(toAccountId);
        createDto.setStatus("settled");
        createDto.setNote(note);
        created.add(create(createDto));
    }

    private boolean isPositive(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    private void validateTxnFields(String txnType, Long accountId, Long categoryId,
                                   Long fromAccountId, Long toAccountId, String status) {
        if (!StringUtils.hasText(txnType) || !TXN_TYPES.contains(txnType)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "交易类型不合法，仅支持 income/expense/transfer/adjustment");
        }
        if (!StringUtils.hasText(status) || !STATUSES.contains(status)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "状态不合法，仅支持 settled/pending");
        }
        if ("pending".equals(status) && !"income".equals(txnType) && !"transfer".equals(txnType)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "仅收入或转账流水允许待入账状态");
        }
        if ("income".equals(txnType) || "expense".equals(txnType)) {
            if (accountId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "收入/支出必须指定账户");
            }
            if (categoryId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "收入/支出必须指定分类");
            }
        } else if ("transfer".equals(txnType)) {
            if (fromAccountId == null || toAccountId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "转账必须指定转出与转入账户");
            }
            if (Objects.equals(fromAccountId, toAccountId)) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "转出与转入账户不能相同");
            }
        } else if ("adjustment".equals(txnType)) {
            if (accountId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "平账必须指定账户");
            }
            if (!"settled".equals(status)) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "平账状态须为已入账");
            }
        }
    }

    private void normalizeByTxnType(FinanceTransactionEntity entity) {
        if ("income".equals(entity.getTxnType()) || "expense".equals(entity.getTxnType())) {
            entity.setFromAccountId(null);
            entity.setToAccountId(null);
        } else if ("transfer".equals(entity.getTxnType())) {
            entity.setAccountId(null);
            entity.setCategoryId(null);
        } else if ("adjustment".equals(entity.getTxnType())) {
            entity.setFromAccountId(null);
            entity.setToAccountId(null);
            entity.setCategoryId(null);
            entity.setStatus("settled");
        }
    }

    private void ensureAccountExists(Long accountId, Long userId, boolean requireEnabled) {
        if (accountId == null) {
            return;
        }
        FinanceAccountEntity account = financeAccountMapper.selectById(accountId);
        if (account == null || !userId.equals(account.getUserId())) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "账户不存在：" + accountId);
        }
        if (requireEnabled && !Integer.valueOf(1).equals(account.getEnabled())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "账户已禁用：" + account.getName());
        }
    }

    private void ensureCategoryExists(Long categoryId, Long userId, boolean requireEnabled, String txnType) {
        if (categoryId == null) {
            return;
        }
        FinanceCategoryEntity category = financeCategoryMapper.selectById(categoryId);
        if (category == null || !userId.equals(category.getUserId())) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "分类不存在：" + categoryId);
        }
        if (requireEnabled && !Integer.valueOf(1).equals(category.getEnabled())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "分类已禁用：" + category.getName());
        }
        if (("income".equals(txnType) || "expense".equals(txnType))
                && StringUtils.hasText(category.getDirection())
                && !txnType.equals(category.getDirection())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "分类方向与交易类型不一致");
        }
    }

    private BigDecimal scaleMoney(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
