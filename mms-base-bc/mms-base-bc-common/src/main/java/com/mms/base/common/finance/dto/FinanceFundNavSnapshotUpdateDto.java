package com.mms.base.common.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 实现功能【更新基金净值快照 DTO】
 *
 * @author li.hongyu
 * @date 2026-08-01
 */
@Data
@Schema(description = "更新净值快照；若该条为最新净值日则同步持仓当前估值")
public class FinanceFundNavSnapshotUpdateDto {

    @NotNull(message = "快照ID不能为空")
    @Schema(description = "快照ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "净值日期")
    private LocalDate navDate;

    @Schema(description = "净值")
    private BigDecimal nav;

    @Schema(description = "市值")
    private BigDecimal marketValue;

    @Schema(description = "估值状态 confirmed/delayed")
    private String quoteStatus;
}
