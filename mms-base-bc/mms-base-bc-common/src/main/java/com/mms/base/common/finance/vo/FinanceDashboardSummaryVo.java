package com.mms.base.common.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 实现功能【记账看板汇总响应 VO】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Data
@Schema(description = "记账看板汇总响应对象")
public class FinanceDashboardSummaryVo {

    @Schema(description = "本月收入（已入账）")
    private BigDecimal monthIncome;

    @Schema(description = "本月支出（已入账）")
    private BigDecimal monthExpense;

    @Schema(description = "本月结余 = 收入 - 支出")
    private BigDecimal monthBalance;

    @Schema(description = "待入账收入金额")
    private BigDecimal pendingAmount;

    @Schema(description = "总资产 = 全部账户账面余额（含基金壳）")
    private BigDecimal totalAsset;

    @Schema(description = "基金账户账面余额合计")
    private BigDecimal fundAsset;

    @Schema(description = "待入账转账金额")
    private BigDecimal pendingTransferAmount;

    @Schema(description = "账户余额列表")
    private List<FinanceAccountBalanceVo> accounts;

    @Schema(description = "近 N 日收支趋势")
    private List<FinanceDailyTrendVo> dailyTrends;

    @Schema(description = "本月支出分类统计")
    private List<FinanceCategoryStatVo> expenseCategoryStats;
}
