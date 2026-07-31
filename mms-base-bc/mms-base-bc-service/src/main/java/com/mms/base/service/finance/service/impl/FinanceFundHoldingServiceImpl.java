package com.mms.base.service.finance.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.finance.dto.*;
import com.mms.base.common.finance.entity.FinanceAccountEntity;
import com.mms.base.common.finance.entity.FinanceFundHoldingEntity;
import com.mms.base.common.finance.entity.FinanceFundNavSnapshotEntity;
import com.mms.base.common.finance.entity.FinanceTransactionEntity;
import com.mms.base.common.finance.vo.FinanceFundHoldingVo;
import com.mms.base.common.finance.vo.FinanceFundRedeemResultVo;
import com.mms.base.common.finance.vo.FinanceTransactionVo;
import com.mms.base.common.system.vo.DictDataVo;
import com.mms.base.service.finance.mapper.FinanceAccountMapper;
import com.mms.base.service.finance.mapper.FinanceFundHoldingMapper;
import com.mms.base.service.finance.mapper.FinanceFundNavSnapshotMapper;
import com.mms.base.service.finance.mapper.FinanceTransactionMapper;
import com.mms.base.service.finance.service.FinanceFundHoldingService;
import com.mms.base.service.finance.support.FinanceUserSupport;
import com.mms.base.service.system.service.DictDataService;
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
import java.util.List;
import java.util.Set;

/**
 * 实现功能【基金持仓服务实现类】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Slf4j
@Service
public class FinanceFundHoldingServiceImpl implements FinanceFundHoldingService {

    private static final String DICT_FUND_CATEGORY = "finance_fund_category";
    private static final String DICT_QUOTE_STATUS = "finance_fund_quote_status";
    private static final String ACCOUNT_TYPE_FUND = "fund";
    private static final Set<String> QUOTE_STATUSES = Set.of("confirmed", "delayed");

    @Resource
    private FinanceFundHoldingMapper financeFundHoldingMapper;

    @Resource
    private FinanceFundNavSnapshotMapper financeFundNavSnapshotMapper;

    @Resource
    private FinanceAccountMapper financeAccountMapper;

    @Resource
    private FinanceTransactionMapper financeTransactionMapper;

    @Resource
    private DictDataService dictDataService;

    @Override
    public Page<FinanceFundHoldingVo> getHoldingPage(FinanceFundHoldingPageQueryDto dto) {
        try {
            Long userId = FinanceUserSupport.requireUserId();
            Page<FinanceFundHoldingVo> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            return financeFundHoldingMapper.getHoldingPage(page, dto, userId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("分页查询基金持仓失败：{}", e.getMessage(), e);
            throw new ServerException("查询基金持仓列表失败", e);
        }
    }

    @Override
    public FinanceFundHoldingVo getById(Long id) {
        try {
            Long userId = FinanceUserSupport.requireUserId();
            if (id == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "持仓ID不能为空");
            }
            FinanceFundHoldingVo vo = financeFundHoldingMapper.getHoldingById(id, userId);
            if (vo == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "持仓不存在");
            }
            return vo;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询基金持仓详情失败：{}", e.getMessage(), e);
            throw new ServerException("查询基金持仓详情失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceFundHoldingVo create(FinanceFundHoldingCreateDto dto) {
        try {
            Long userId = FinanceUserSupport.requireUserId();
            log.info("创建基金持仓，userId={}，参数：{}", userId, dto);
            ensureFundAccount(dto.getAccountId(), userId);
            validateFundCategory(dto.getFundCategory());
            String quoteStatus = normalizeQuoteStatus(dto.getQuoteStatus());

            BigDecimal shares = scaleShares(dto.getShares());
            BigDecimal costAmount = scaleMoney(dto.getCostAmount());
            BigDecimal nav = scaleNav(dto.getNav());
            BigDecimal marketValue = resolveMarketValue(dto.getMarketValue(), shares, nav);

            FinanceFundHoldingEntity entity = new FinanceFundHoldingEntity();
            entity.setUserId(userId);
            entity.setAccountId(dto.getAccountId());
            entity.setFundCode(trimToNull(dto.getFundCode()));
            entity.setFundName(dto.getFundName().trim());
            entity.setFundCategory(dto.getFundCategory());
            entity.setShares(shares);
            entity.setCostAmount(costAmount);
            entity.setNav(nav);
            entity.setNavDate(dto.getNavDate());
            entity.setMarketValue(marketValue);
            entity.setQuoteStatus(quoteStatus);
            entity.setEstimatedMarketValue(scaleMoney(dto.getEstimatedMarketValue()));
            entity.setNote(dto.getNote());
            entity.setSortOrder(dto.getSortOrder() == null ? 0 : dto.getSortOrder());
            entity.setEnabled(dto.getEnabled() == null ? 1 : dto.getEnabled());
            financeFundHoldingMapper.insert(entity);

            if (nav != null && dto.getNavDate() != null) {
                insertSnapshot(userId, entity.getId(), dto.getNavDate(), nav, marketValue, quoteStatus);
            }
            return financeFundHoldingMapper.getHoldingById(entity.getId(), userId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建基金持仓失败：{}", e.getMessage(), e);
            throw new ServerException("创建基金持仓失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceFundHoldingVo update(FinanceFundHoldingUpdateDto dto) {
        try {
            Long userId = FinanceUserSupport.requireUserId();
            FinanceFundHoldingEntity entity = requireHolding(dto.getId(), userId);
            if (dto.getAccountId() != null) {
                ensureFundAccount(dto.getAccountId(), userId);
                entity.setAccountId(dto.getAccountId());
            }
            if (dto.getFundCode() != null) {
                entity.setFundCode(trimToNull(dto.getFundCode()));
            }
            if (StringUtils.hasText(dto.getFundName())) {
                entity.setFundName(dto.getFundName().trim());
            }
            if (StringUtils.hasText(dto.getFundCategory())) {
                validateFundCategory(dto.getFundCategory());
                entity.setFundCategory(dto.getFundCategory());
            }
            if (dto.getShares() != null) {
                entity.setShares(scaleShares(dto.getShares()));
            }
            if (dto.getCostAmount() != null) {
                entity.setCostAmount(scaleMoney(dto.getCostAmount()));
            }
            if (dto.getNav() != null) {
                entity.setNav(scaleNav(dto.getNav()));
            }
            if (dto.getNavDate() != null) {
                entity.setNavDate(dto.getNavDate());
            }
            if (dto.getMarketValue() != null) {
                entity.setMarketValue(scaleMoney(dto.getMarketValue()));
            } else if (dto.getNav() != null || dto.getShares() != null) {
                entity.setMarketValue(resolveMarketValue(null, entity.getShares(), entity.getNav()));
            }
            if (StringUtils.hasText(dto.getQuoteStatus())) {
                entity.setQuoteStatus(normalizeQuoteStatus(dto.getQuoteStatus()));
            }
            if (dto.getEstimatedMarketValue() != null) {
                entity.setEstimatedMarketValue(scaleMoney(dto.getEstimatedMarketValue()));
            }
            if (dto.getNote() != null) {
                entity.setNote(dto.getNote());
            }
            if (dto.getSortOrder() != null) {
                entity.setSortOrder(dto.getSortOrder());
            }
            if (dto.getEnabled() != null) {
                entity.setEnabled(dto.getEnabled());
            }
            financeFundHoldingMapper.updateById(entity);
            return financeFundHoldingMapper.getHoldingById(entity.getId(), userId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新基金持仓失败：{}", e.getMessage(), e);
            throw new ServerException("更新基金持仓失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        try {
            Long userId = FinanceUserSupport.requireUserId();
            FinanceFundHoldingEntity entity = requireHolding(id, userId);
            financeFundHoldingMapper.deleteById(entity.getId());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除基金持仓失败：{}", e.getMessage(), e);
            throw new ServerException("删除基金持仓失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(FinanceFundHoldingBatchDeleteDto dto) {
        if (dto.getIds() == null || dto.getIds().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "持仓ID列表不能为空");
        }
        for (Long id : dto.getIds()) {
            delete(id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceFundHoldingVo purchase(FinanceFundPurchaseDto dto) {
        try {
            Long userId = FinanceUserSupport.requireUserId();
            FinanceFundHoldingEntity entity = requireHolding(dto.getHoldingId(), userId);
            BigDecimal buyShares = scaleShares(dto.getShares());
            BigDecimal buyAmount = scaleMoney(dto.getAmount());
            BigDecimal oldShares = nullToZeroShares(entity.getShares());
            BigDecimal oldCost = nullToZeroMoney(entity.getCostAmount());

            entity.setShares(oldShares.add(buyShares));
            entity.setCostAmount(oldCost.add(buyAmount));
            if (dto.getNav() != null) {
                entity.setNav(scaleNav(dto.getNav()));
            }
            if (dto.getNavDate() != null) {
                entity.setNavDate(dto.getNavDate());
            }
            entity.setMarketValue(resolveMarketValue(null, entity.getShares(), entity.getNav()));
            if (StringUtils.hasText(dto.getNote())) {
                entity.setNote(dto.getNote());
            }
            financeFundHoldingMapper.updateById(entity);

            if (dto.getFromAccountId() != null) {
                ensureAccountOwned(dto.getFromAccountId(), userId, "扣款账户不存在");
                FinanceTransactionEntity txn = new FinanceTransactionEntity();
                txn.setUserId(userId);
                txn.setTxnDate(dto.getTxnDate() == null ? LocalDate.now() : dto.getTxnDate());
                txn.setTxnType("transfer");
                txn.setAmount(buyAmount);
                txn.setFromAccountId(dto.getFromAccountId());
                txn.setToAccountId(entity.getAccountId());
                txn.setStatus("settled");
                txn.setNote(StringUtils.hasText(dto.getNote())
                        ? dto.getNote()
                        : String.format("基金申购：%s", entity.getFundName()));
                financeTransactionMapper.insert(txn);
            }
            return financeFundHoldingMapper.getHoldingById(entity.getId(), userId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("基金申购失败：{}", e.getMessage(), e);
            throw new ServerException("基金申购失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceFundRedeemResultVo redeem(FinanceFundRedeemDto dto) {
        try {
            Long userId = FinanceUserSupport.requireUserId();
            FinanceFundHoldingEntity entity = requireHolding(dto.getHoldingId(), userId);
            ensureAccountOwned(dto.getToAccountId(), userId, "到账账户不存在");
            BigDecimal redeemShares = scaleShares(dto.getShares());
            BigDecimal oldShares = nullToZeroShares(entity.getShares());
            if (redeemShares.compareTo(oldShares) > 0) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "赎回份额不能大于持有份额");
            }
            BigDecimal oldCost = nullToZeroMoney(entity.getCostAmount());
            BigDecimal remainShares = oldShares.subtract(redeemShares);
            BigDecimal remainCost = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            if (remainShares.compareTo(BigDecimal.ZERO) > 0 && oldShares.compareTo(BigDecimal.ZERO) > 0) {
                remainCost = oldCost.multiply(remainShares)
                        .divide(oldShares, 2, RoundingMode.HALF_UP);
            }
            entity.setShares(remainShares);
            entity.setCostAmount(remainCost);
            entity.setMarketValue(resolveMarketValue(null, entity.getShares(), entity.getNav()));
            if (StringUtils.hasText(dto.getNote())) {
                entity.setNote(dto.getNote());
            }
            financeFundHoldingMapper.updateById(entity);

            FinanceTransactionEntity txn = new FinanceTransactionEntity();
            txn.setUserId(userId);
            txn.setTxnDate(dto.getTxnDate() == null ? LocalDate.now() : dto.getTxnDate());
            txn.setTxnType("transfer");
            txn.setAmount(scaleMoney(dto.getAmount()));
            txn.setFromAccountId(entity.getAccountId());
            txn.setToAccountId(dto.getToAccountId());
            txn.setStatus("pending");
            txn.setNote(StringUtils.hasText(dto.getNote())
                    ? dto.getNote()
                    : String.format("基金赎回待到账：%s", entity.getFundName()));
            financeTransactionMapper.insert(txn);

            FinanceFundRedeemResultVo result = new FinanceFundRedeemResultVo();
            result.setHolding(financeFundHoldingMapper.getHoldingById(entity.getId(), userId));
            result.setPendingTransactionId(txn.getId());
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("基金赎回失败：{}", e.getMessage(), e);
            throw new ServerException("基金赎回失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceTransactionVo settleRedeem(FinanceFundSettleRedeemDto dto) {
        try {
            Long userId = FinanceUserSupport.requireUserId();
            FinanceTransactionEntity entity = financeTransactionMapper.selectById(dto.getTransactionId());
            if (entity == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "流水不存在");
            }
            FinanceUserSupport.requireOwned(entity.getUserId(), "流水不存在");
            if (!"transfer".equals(entity.getTxnType()) || !"pending".equals(entity.getStatus())) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "仅支持确认待到账的赎回转账");
            }
            if (dto.getActualAmount() != null) {
                entity.setAmount(scaleMoney(dto.getActualAmount()));
            }
            if (dto.getSettleDate() != null) {
                entity.setTxnDate(dto.getSettleDate());
            }
            if (dto.getNote() != null) {
                entity.setNote(dto.getNote());
            }
            entity.setStatus("settled");
            financeTransactionMapper.updateById(entity);
            return financeTransactionMapper.getTransactionById(entity.getId(), userId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("确认赎回到账失败：{}", e.getMessage(), e);
            throw new ServerException("确认赎回到账失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceFundHoldingVo updateValuation(FinanceFundValuationDto dto) {
        try {
            Long userId = FinanceUserSupport.requireUserId();
            FinanceFundHoldingEntity entity = requireHolding(dto.getHoldingId(), userId);
            if (dto.getNav() == null && dto.getMarketValue() == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "净值与市值至少填写一项");
            }
            if (dto.getNav() != null) {
                entity.setNav(scaleNav(dto.getNav()));
            }
            LocalDate navDate = dto.getNavDate() == null ? LocalDate.now() : dto.getNavDate();
            entity.setNavDate(navDate);
            BigDecimal marketValue = resolveMarketValue(dto.getMarketValue(), entity.getShares(), entity.getNav());
            if (marketValue == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "无法计算市值，请填写市值或净值");
            }
            entity.setMarketValue(marketValue);
            String quoteStatus = StringUtils.hasText(dto.getQuoteStatus())
                    ? normalizeQuoteStatus(dto.getQuoteStatus())
                    : entity.getQuoteStatus();
            entity.setQuoteStatus(quoteStatus);
            if (dto.getEstimatedMarketValue() != null) {
                entity.setEstimatedMarketValue(scaleMoney(dto.getEstimatedMarketValue()));
            }
            financeFundHoldingMapper.updateById(entity);
            if (entity.getNav() != null) {
                insertSnapshot(userId, entity.getId(), navDate, entity.getNav(), marketValue, quoteStatus);
            }
            return financeFundHoldingMapper.getHoldingById(entity.getId(), userId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新基金估值失败：{}", e.getMessage(), e);
            throw new ServerException("更新基金估值失败", e);
        }
    }

    private void insertSnapshot(Long userId, Long holdingId, LocalDate navDate,
                                BigDecimal nav, BigDecimal marketValue, String quoteStatus) {
        FinanceFundNavSnapshotEntity snapshot = new FinanceFundNavSnapshotEntity();
        snapshot.setUserId(userId);
        snapshot.setHoldingId(holdingId);
        snapshot.setNavDate(navDate);
        snapshot.setNav(nav);
        snapshot.setMarketValue(marketValue);
        snapshot.setQuoteStatus(quoteStatus);
        financeFundNavSnapshotMapper.insert(snapshot);
    }

    private FinanceFundHoldingEntity requireHolding(Long id, Long userId) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "持仓ID不能为空");
        }
        FinanceFundHoldingEntity entity = financeFundHoldingMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "持仓不存在");
        }
        FinanceUserSupport.requireOwned(entity.getUserId(), "持仓不存在");
        return entity;
    }

    private void ensureFundAccount(Long accountId, Long userId) {
        FinanceAccountEntity account = financeAccountMapper.selectById(accountId);
        if (account == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "基金账户不存在");
        }
        FinanceUserSupport.requireOwned(account.getUserId(), "基金账户不存在");
        if (!ACCOUNT_TYPE_FUND.equals(account.getAccountType())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "请选择类型为「基金」的账户作为持仓壳");
        }
        if (account.getEnabled() != null && account.getEnabled() == 0) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "基金账户已禁用");
        }
    }

    private void ensureAccountOwned(Long accountId, Long userId, String notFoundMsg) {
        FinanceAccountEntity account = financeAccountMapper.selectById(accountId);
        if (account == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, notFoundMsg);
        }
        FinanceUserSupport.requireOwned(account.getUserId(), notFoundMsg);
    }

    private void validateFundCategory(String category) {
        List<DictDataVo> dictList = dictDataService.getDictDataListByTypeCode(DICT_FUND_CATEGORY);
        boolean valid = dictList != null && dictList.stream()
                .anyMatch(item -> category.equals(item.getDictValue()));
        if (!valid) {
            throw new BusinessException(ErrorCode.PARAM_INVALID,
                    "基金分类不合法，请在数据字典【finance_fund_category】中维护");
        }
    }

    private String normalizeQuoteStatus(String quoteStatus) {
        String status = StringUtils.hasText(quoteStatus) ? quoteStatus : "confirmed";
        if (!QUOTE_STATUSES.contains(status)) {
            List<DictDataVo> dictList = dictDataService.getDictDataListByTypeCode(DICT_QUOTE_STATUS);
            boolean valid = dictList != null && dictList.stream()
                    .anyMatch(item -> status.equals(item.getDictValue()));
            if (!valid) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "估值状态仅支持 confirmed/delayed");
            }
        }
        return status;
    }

    private BigDecimal resolveMarketValue(BigDecimal marketValue, BigDecimal shares, BigDecimal nav) {
        if (marketValue != null) {
            return scaleMoney(marketValue);
        }
        if (shares != null && nav != null) {
            return shares.multiply(nav).setScale(2, RoundingMode.HALF_UP);
        }
        return scaleMoney(BigDecimal.ZERO);
    }

    private BigDecimal scaleMoney(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal scaleShares(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return value.setScale(6, RoundingMode.HALF_UP);
    }

    private BigDecimal scaleNav(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return value.setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal nullToZeroMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : value;
    }

    private BigDecimal nullToZeroShares(BigDecimal value) {
        return value == null ? BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP) : value;
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
