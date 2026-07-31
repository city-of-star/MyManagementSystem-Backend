package com.mms.base.common.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 实现功能【基金账户壳下已确认市值】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Data
@Schema(description = "基金账户壳下已确认市值")
public class FinanceFundAccountMarketVo {

    @Schema(description = "基金账户壳 ID")
    private Long accountId;

    @Schema(description = "该壳下已确认持仓市值合计")
    private BigDecimal confirmedMarketValue;
}
