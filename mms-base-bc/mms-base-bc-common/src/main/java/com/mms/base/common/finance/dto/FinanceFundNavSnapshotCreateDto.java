package com.mms.base.common.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 实现功能【新增基金净值快照 DTO】
 *
 * @author li.hongyu
 * @date 2026-08-01
 */
@Data
@Schema(description = "新增净值快照；若净值日为最新则同步持仓当前估值")
public class FinanceFundNavSnapshotCreateDto {

    @NotNull(message = "持仓ID不能为空")
    @Schema(description = "持仓ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long holdingId;

    @NotNull(message = "净值日期不能为空")
    @Schema(description = "净值日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate navDate;

    @Schema(description = "净值；与市值至少填一个")
    private BigDecimal nav;

    @Schema(description = "市值；不填则份额×净值")
    private BigDecimal marketValue;

    @Schema(description = "估值状态 confirmed/delayed")
    private String quoteStatus;
}
