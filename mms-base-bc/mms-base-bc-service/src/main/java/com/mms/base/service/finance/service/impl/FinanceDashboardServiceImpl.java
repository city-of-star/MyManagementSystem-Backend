package com.mms.base.service.finance.service.impl;

import com.mms.base.common.finance.vo.FinanceAccountBalanceVo;
import com.mms.base.common.finance.vo.FinanceCategoryStatVo;
import com.mms.base.common.finance.vo.FinanceDailyTrendVo;
import com.mms.base.common.finance.vo.FinanceDashboardSummaryVo;
import com.mms.base.common.finance.vo.FinanceFundHoldingSummaryVo;
import com.mms.base.service.finance.mapper.FinanceAccountMapper;
import com.mms.base.service.finance.mapper.FinanceFundHoldingMapper;
import com.mms.base.service.finance.mapper.FinanceTransactionMapper;
import com.mms.base.service.finance.service.FinanceDashboardService;
import com.mms.base.service.finance.service.FinanceInitService;
import com.mms.base.service.finance.support.FinanceUserSupport;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 实现功能【记账看板服务实现类】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Slf4j
@Service
public class FinanceDashboardServiceImpl implements FinanceDashboardService {

    @Resource
    private FinanceTransactionMapper financeTransactionMapper;

    @Resource
    private FinanceAccountMapper financeAccountMapper;

    @Resource
    private FinanceInitService financeInitService;

    @Resource
    private FinanceFundHoldingMapper financeFundHoldingMapper;

    @Override
    public FinanceDashboardSummaryVo getSummary(Integer days) {
        try {
            financeInitService.ensureInitialized();
            Long userId = FinanceUserSupport.requireUserId();
            int range = days == null || days < 1 ? 30 : Math.min(days, 90);
            LocalDate today = LocalDate.now();
            LocalDate monthStart = today.withDayOfMonth(1);
            LocalDate trendStart = today.minusDays(range - 1L);

            BigDecimal monthIncome = nullToZero(
                    financeTransactionMapper.sumAmount("income", "settled", monthStart, today, userId));
            BigDecimal monthExpense = nullToZero(
                    financeTransactionMapper.sumAmount("expense", "settled", monthStart, today, userId));
            BigDecimal pendingAmount = nullToZero(
                    financeTransactionMapper.sumAmount("income", "pending", null, null, userId));
            BigDecimal pendingTransferAmount = nullToZero(
                    financeTransactionMapper.sumAmount("transfer", "pending", null, null, userId));

            List<FinanceAccountBalanceVo> accounts = financeAccountMapper.listAccountBalances(1, userId);
            BigDecimal nonFundAsset = accounts.stream()
                    .filter(item -> item.getAccountType() == null || !"fund".equals(item.getAccountType()))
                    .map(FinanceAccountBalanceVo::getBalance)
                    .filter(balance -> balance != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            FinanceFundHoldingSummaryVo fundSummary = financeFundHoldingMapper.sumHoldings(userId);
            BigDecimal fundConfirmed = fundSummary == null || fundSummary.getConfirmedMarketValue() == null
                    ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                    : fundSummary.getConfirmedMarketValue().setScale(2, RoundingMode.HALF_UP);
            BigDecimal fundLagged = fundSummary == null || fundSummary.getLaggedMarketValue() == null
                    ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                    : fundSummary.getLaggedMarketValue().setScale(2, RoundingMode.HALF_UP);

            BigDecimal totalAsset = nonFundAsset.add(fundConfirmed).setScale(2, RoundingMode.HALF_UP);

            List<FinanceDailyTrendVo> rawTrends = financeTransactionMapper.listDailyTrends(trendStart, today, userId);
            Map<LocalDate, FinanceDailyTrendVo> trendMap = rawTrends.stream()
                    .collect(Collectors.toMap(FinanceDailyTrendVo::getDate, item -> item, (a, b) -> a));
            List<FinanceDailyTrendVo> dailyTrends = new ArrayList<>();
            for (LocalDate date = trendStart; !date.isAfter(today); date = date.plusDays(1)) {
                FinanceDailyTrendVo item = trendMap.get(date);
                if (item == null) {
                    item = new FinanceDailyTrendVo();
                    item.setDate(date);
                    item.setIncomeAmount(BigDecimal.ZERO);
                    item.setExpenseAmount(BigDecimal.ZERO);
                }
                dailyTrends.add(item);
            }

            List<FinanceCategoryStatVo> expenseCategoryStats =
                    financeTransactionMapper.listExpenseCategoryStats(monthStart, today, 10, userId);

            FinanceDashboardSummaryVo summary = new FinanceDashboardSummaryVo();
            summary.setMonthIncome(monthIncome);
            summary.setMonthExpense(monthExpense);
            summary.setMonthBalance(monthIncome.subtract(monthExpense).setScale(2, RoundingMode.HALF_UP));
            summary.setPendingAmount(pendingAmount);
            summary.setPendingTransferAmount(pendingTransferAmount);
            summary.setFundConfirmedAsset(fundConfirmed);
            summary.setFundLaggedAsset(fundLagged);
            summary.setTotalAsset(totalAsset);
            summary.setAccounts(accounts);
            summary.setDailyTrends(dailyTrends);
            summary.setExpenseCategoryStats(expenseCategoryStats);
            return summary;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询记账看板汇总失败：{}", e.getMessage(), e);
            throw new ServerException("查询记账看板汇总失败", e);
        }
    }

    private BigDecimal nullToZero(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
