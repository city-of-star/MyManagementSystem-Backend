package com.mms.base.common.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 实现功能【基金持仓汇总 VO】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Data
@Schema(description = "基金持仓汇总")
public class FinanceFundHoldingSummaryVo {

    @Schema(description = "已确认市值合计（计入总资产）")
    private BigDecimal confirmedMarketValue;

    @Schema(description = "滞后待更新市值合计（不计入总资产）")
    private BigDecimal laggedMarketValue;

    @Schema(description = "持仓成本合计")
    private BigDecimal totalCostAmount;
}
