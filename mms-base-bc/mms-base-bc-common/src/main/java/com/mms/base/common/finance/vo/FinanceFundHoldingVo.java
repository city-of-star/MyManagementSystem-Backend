package com.mms.base.common.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 实现功能【基金持仓响应 VO】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Data
@Schema(description = "基金持仓响应对象")
public class FinanceFundHoldingVo {

    @Schema(description = "持仓ID")
    private Long id;

    @Schema(description = "基金账户壳ID")
    private Long accountId;

    @Schema(description = "基金账户名称")
    private String accountName;

    @Schema(description = "基金代码")
    private String fundCode;

    @Schema(description = "基金名称")
    private String fundName;

    @Schema(description = "分类")
    private String fundCategory;

    @Schema(description = "持有份额")
    private BigDecimal shares;

    @Schema(description = "持仓成本合计")
    private BigDecimal costAmount;

    @Schema(description = "单位成本（移动平均）")
    private BigDecimal avgCost;

    @Schema(description = "最近净值")
    private BigDecimal nav;

    @Schema(description = "净值日期")
    private LocalDate navDate;

    @Schema(description = "市值")
    private BigDecimal marketValue;

    @Schema(description = "浮盈浮亏 = 市值 - 成本")
    private BigDecimal profitLoss;

    @Schema(description = "估值状态")
    private String quoteStatus;

    @Schema(description = "滞后估算市值")
    private BigDecimal estimatedMarketValue;

    @Schema(description = "备注")
    private String note;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "是否启用")
    private Integer enabled;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
