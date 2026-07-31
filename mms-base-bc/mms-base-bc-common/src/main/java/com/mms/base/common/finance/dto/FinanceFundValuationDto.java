package com.mms.base.common.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 实现功能【手填基金估值 DTO】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Data
@Schema(description = "手填净值/市值，并写入快照")
public class FinanceFundValuationDto {

    @NotNull(message = "持仓ID不能为空")
    @Schema(description = "持仓ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long holdingId;

    @Schema(description = "净值；与市值至少填一个")
    private BigDecimal nav;

    @Schema(description = "净值日期")
    private LocalDate navDate;

    @Schema(description = "市值；不填则份额×净值")
    private BigDecimal marketValue;

    @Schema(description = "估值状态 confirmed/delayed")
    private String quoteStatus;

    @Schema(description = "滞后估算市值")
    private BigDecimal estimatedMarketValue;
}
