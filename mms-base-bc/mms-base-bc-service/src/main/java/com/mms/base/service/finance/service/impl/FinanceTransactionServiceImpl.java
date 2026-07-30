package com.mms.base.service.finance.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.finance.dto.FinanceTransactionBatchDeleteDto;
import com.mms.base.common.finance.dto.FinanceTransactionCreateDto;
import com.mms.base.common.finance.dto.FinanceTransactionFromRecurringDto;
import com.mms.base.common.finance.dto.FinanceTransactionPageQueryDto;
import com.mms.base.common.finance.dto.FinanceTransactionUpdateDto;
import com.mms.base.common.finance.entity.FinanceAccountEntity;
import com.mms.base.common.finance.entity.FinanceCategoryEntity;
import com.mms.base.common.finance.entity.FinanceRecurringEntity;
import com.mms.base.common.finance.entity.FinanceTransactionEntity;
import com.mms.base.common.finance.vo.FinanceTransactionVo;
import com.mms.base.service.finance.mapper.FinanceAccountMapper;
import com.mms.base.service.finance.mapper.FinanceCategoryMapper;
import com.mms.base.service.finance.mapper.FinanceRecurringMapper;
import com.mms.base.service.finance.mapper.FinanceTransactionMapper;
import com.mms.base.service.finance.service.FinanceTransactionService;
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

    private static final Set<String> TXN_TYPES = Set.of("income", "expense", "transfer");
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
            log.info("分页查询记账流水，参数：{}", dto);
            Page<FinanceTransactionVo> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            return financeTransactionMapper.getTransactionPage(page, dto);
        } catch (Exception e) {
            log.error("分页查询记账流水失败：{}", e.getMessage(), e);
            throw new ServerException("查询记账流水列表失败", e);
        }
    }

    @Override
    public FinanceTransactionVo getById(Long id) {
        try {
            if (id == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "流水ID不能为空");
            }
            FinanceTransactionVo vo = financeTransactionMapper.getTransactionById(id);
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
            log.info("创建记账流水，参数：{}", dto);
            String status = StringUtils.hasText(dto.getStatus()) ? dto.getStatus() : "settled";
            validateTxnFields(dto.getTxnType(), dto.getAccountId(), dto.getCategoryId(),
                    dto.getFromAccountId(), dto.getToAccountId(), status);
            ensureAccountExists(dto.getAccountId());
            ensureAccountExists(dto.getFromAccountId());
            ensureAccountExists(dto.getToAccountId());
            ensureCategoryExists(dto.getCategoryId());

            FinanceTransactionEntity entity = new FinanceTransactionEntity();
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
            return financeTransactionMapper.getTransactionById(entity.getId());
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
            log.info("更新记账流水，参数：{}", dto);
            FinanceTransactionEntity entity = financeTransactionMapper.selectById(dto.getId());
            if (entity == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "流水不存在");
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
            validateTxnFields(entity.getTxnType(), entity.getAccountId(), entity.getCategoryId(),
                    entity.getFromAccountId(), entity.getToAccountId(), entity.getStatus());
            ensureAccountExists(entity.getAccountId());
            ensureAccountExists(entity.getFromAccountId());
            ensureAccountExists(entity.getToAccountId());
            ensureCategoryExists(entity.getCategoryId());
            normalizeByTxnType(entity);
            financeTransactionMapper.updateById(entity);
            return financeTransactionMapper.getTransactionById(entity.getId());
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
            log.info("由周期模板生成流水，参数：{}", dto);
            FinanceRecurringEntity recurring = financeRecurringMapper.selectById(dto.getRecurringId());
            if (recurring == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "周期模板不存在");
            }
            if (!Integer.valueOf(1).equals(recurring.getEnabled())) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "周期模板已禁用，无法生成流水");
            }
            FinanceTransactionCreateDto createDto = new FinanceTransactionCreateDto();
            createDto.setTxnDate(dto.getTxnDate() != null ? dto.getTxnDate() : LocalDate.now());
            createDto.setTxnType(recurring.getDirection());
            createDto.setAmount(dto.getAmount() != null ? dto.getAmount() : recurring.getAmount());
            createDto.setCategoryId(recurring.getCategoryId());
            createDto.setAccountId(recurring.getAccountId());
            createDto.setStatus("settled");
            createDto.setNote(StringUtils.hasText(dto.getNote()) ? dto.getNote() : recurring.getNote());
            return create(createDto);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("由周期模板生成流水失败：{}", e.getMessage(), e);
            throw new ServerException("由周期模板生成流水失败", e);
        }
    }

    private void validateTxnFields(String txnType, Long accountId, Long categoryId,
                                   Long fromAccountId, Long toAccountId, String status) {
        if (!StringUtils.hasText(txnType) || !TXN_TYPES.contains(txnType)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "交易类型不合法，仅支持 income/expense/transfer");
        }
        if (!StringUtils.hasText(status) || !STATUSES.contains(status)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "状态不合法，仅支持 settled/pending");
        }
        if ("pending".equals(status) && !"income".equals(txnType)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "仅收入流水允许待入账状态");
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
        }
    }

    private void normalizeByTxnType(FinanceTransactionEntity entity) {
        if ("income".equals(entity.getTxnType()) || "expense".equals(entity.getTxnType())) {
            entity.setFromAccountId(null);
            entity.setToAccountId(null);
        } else if ("transfer".equals(entity.getTxnType())) {
            entity.setAccountId(null);
        }
    }

    private void ensureAccountExists(Long accountId) {
        if (accountId == null) {
            return;
        }
        FinanceAccountEntity account = financeAccountMapper.selectById(accountId);
        if (account == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "账户不存在：" + accountId);
        }
    }

    private void ensureCategoryExists(Long categoryId) {
        if (categoryId == null) {
            return;
        }
        FinanceCategoryEntity category = financeCategoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "分类不存在：" + categoryId);
        }
    }

    private BigDecimal scaleMoney(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
