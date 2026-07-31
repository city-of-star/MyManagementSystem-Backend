package com.mms.base.common.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 实现功能【更新基金持仓请求 DTO】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Data
@Schema(description = "更新基金持仓请求参数")
public class FinanceFundHoldingUpdateDto {

    @NotNull(message = "持仓ID不能为空")
    @Schema(description = "持仓ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "基金账户壳ID")
    private Long accountId;

    @Size(max = 32, message = "基金代码长度不能超过32个字符")
    @Schema(description = "基金代码")
    private String fundCode;

    @Size(max = 128, message = "基金名称长度不能超过128个字符")
    @Schema(description = "基金名称")
    private String fundName;

    @Size(max = 32, message = "基金分类长度不能超过32个字符")
    @Schema(description = "分类")
    private String fundCategory;

    @Schema(description = "持有份额")
    private BigDecimal shares;

    @Schema(description = "持仓成本合计")
    private BigDecimal costAmount;

    @Schema(description = "净值")
    private BigDecimal nav;

    @Schema(description = "净值日期")
    private LocalDate navDate;

    @Schema(description = "市值")
    private BigDecimal marketValue;

    @Schema(description = "估值状态")
    private String quoteStatus;

    @Schema(description = "滞后估算市值")
    private BigDecimal estimatedMarketValue;

    @Size(max = 512, message = "备注长度不能超过512个字符")
    @Schema(description = "备注")
    private String note;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "是否启用：1/0")
    private Integer enabled;
}
