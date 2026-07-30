package com.mms.base.common.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 实现功能【记账日趋势响应 VO】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Data
@Schema(description = "记账日趋势响应对象")
public class FinanceDailyTrendVo {

    @Schema(description = "日期")
    private LocalDate date;

    @Schema(description = "当日收入金额")
    private BigDecimal incomeAmount;

    @Schema(description = "当日支出金额")
    private BigDecimal expenseAmount;
}
