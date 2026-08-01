package com.mms.base.common.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 实现功能【基金净值快照 VO】
 *
 * @author li.hongyu
 * @date 2026-08-01
 */
@Data
@Schema(description = "基金净值快照")
public class FinanceFundNavSnapshotVo {

    @Schema(description = "快照ID")
    private Long id;

    @Schema(description = "持仓ID")
    private Long holdingId;

    @Schema(description = "净值日期")
    private LocalDate navDate;

    @Schema(description = "净值")
    private BigDecimal nav;

    @Schema(description = "市值")
    private BigDecimal marketValue;

    @Schema(description = "估值状态")
    private String quoteStatus;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
