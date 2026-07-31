package com.mms.base.common.finance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 实现功能【基金净值快照实体】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("finance_fund_nav_snapshot")
@Schema(description = "基金净值快照实体")
public class FinanceFundNavSnapshotEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("user_id")
    @Schema(description = "归属用户ID")
    private Long userId;

    @TableField("holding_id")
    @Schema(description = "持仓ID")
    private Long holdingId;

    @TableField("nav_date")
    @Schema(description = "净值日期")
    private LocalDate navDate;

    @Schema(description = "净值")
    private BigDecimal nav;

    @TableField("market_value")
    @Schema(description = "市值")
    private BigDecimal marketValue;

    @TableField("quote_status")
    @Schema(description = "估值状态")
    private String quoteStatus;
}
