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
 * 实现功能【基金持仓实体】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("finance_fund_holding")
@Schema(description = "基金持仓实体")
public class FinanceFundHoldingEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("user_id")
    @Schema(description = "归属用户ID")
    private Long userId;

    @TableField("account_id")
    @Schema(description = "基金账户壳ID")
    private Long accountId;

    @TableField("fund_code")
    @Schema(description = "基金代码")
    private String fundCode;

    @TableField("fund_name")
    @Schema(description = "基金名称")
    private String fundName;

    @TableField("fund_category")
    @Schema(description = "分类（字典 finance_fund_category）")
    private String fundCategory;

    @Schema(description = "持有份额")
    private BigDecimal shares;

    @TableField("cost_amount")
    @Schema(description = "持仓成本合计")
    private BigDecimal costAmount;

    @Schema(description = "最近净值")
    private BigDecimal nav;

    @TableField("nav_date")
    @Schema(description = "净值日期")
    private LocalDate navDate;

    @TableField("market_value")
    @Schema(description = "最近市值")
    private BigDecimal marketValue;

    @TableField("quote_status")
    @Schema(description = "估值状态：confirmed/delayed")
    private String quoteStatus;

    @TableField("estimated_market_value")
    @Schema(description = "滞后估算市值")
    private BigDecimal estimatedMarketValue;

    @Schema(description = "备注")
    private String note;

    @TableField("sort_order")
    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "是否启用：1/0")
    private Integer enabled;
}
