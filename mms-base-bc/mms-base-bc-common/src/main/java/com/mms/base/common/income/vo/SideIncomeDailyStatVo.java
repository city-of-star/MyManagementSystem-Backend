package com.mms.base.common.income.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 实现功能【副业收入按日统计 VO】
 *
 * @author li.hongyu
 * @date 2026-07-29
 */
@Data
@Schema(description = "副业收入按日统计响应对象")
public class SideIncomeDailyStatVo {

    @Schema(description = "日期")
    private LocalDate recordDate;

    @Schema(description = "当日已到账金额")
    private BigDecimal receivedAmount = BigDecimal.ZERO;

    @Schema(description = "当日待结算金额")
    private BigDecimal pendingAmount = BigDecimal.ZERO;
}
