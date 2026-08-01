package com.mms.base.common.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 实现功能【基金净值快照分页查询 DTO】
 *
 * @author li.hongyu
 * @date 2026-08-01
 */
@Data
@Schema(description = "基金净值快照分页查询")
public class FinanceFundNavSnapshotPageQueryDto {

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;

    @NotNull(message = "持仓ID不能为空")
    @Schema(description = "持仓ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long holdingId;
}
