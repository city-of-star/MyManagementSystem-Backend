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

    @Schema(description = "总资产 = 非基金账户余额 + 基金已确认市值（方案甲）")
    private BigDecimal totalAsset;

    @Schema(description = "基金已确认市值合计")
    private BigDecimal fundConfirmedAsset;

    @Schema(description = "基金滞后待更新市值合计（不计入总资产）")
    private BigDecimal fundLaggedAsset;

    @Schema(description = "待入账转账金额（含赎回到账中）")
    private BigDecimal pendingTransferAmount;

    @Schema(description = "账户余额列表（基金壳余额仅供参考，总资产不重复计入）")
    private List<FinanceAccountBalanceVo> accounts;

    @Schema(description = "近 N 日收支趋势")
    private List<FinanceDailyTrendVo> dailyTrends;

    @Schema(description = "本月支出分类统计")
    private List<FinanceCategoryStatVo> expenseCategoryStats;
}
