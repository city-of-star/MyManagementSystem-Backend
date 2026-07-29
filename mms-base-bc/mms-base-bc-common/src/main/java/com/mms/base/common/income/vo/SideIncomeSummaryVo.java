package com.mms.base.common.income.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 实现功能【副业收入汇总 VO】
 *
 * @author li.hongyu
 * @date 2026-07-29
 */
@Data
@Schema(description = "副业收入汇总响应对象")
public class SideIncomeSummaryVo {

    @Schema(description = "今日已到账")
    private BigDecimal todayReceived = BigDecimal.ZERO;

    @Schema(description = "本月已到账")
    private BigDecimal monthReceived = BigDecimal.ZERO;

    @Schema(description = "本月流水（应得合计，含待结算）")
    private BigDecimal monthTotal = BigDecimal.ZERO;

    @Schema(description = "待结算金额")
    private BigDecimal pendingAmount = BigDecimal.ZERO;

    @Schema(description = "累计已到账")
    private BigDecimal totalReceived = BigDecimal.ZERO;
}
