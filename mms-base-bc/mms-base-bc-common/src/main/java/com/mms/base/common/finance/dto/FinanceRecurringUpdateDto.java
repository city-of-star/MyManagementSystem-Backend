package com.mms.base.common.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 实现功能【更新周期记账模板请求 DTO】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Data
@Schema(description = "更新周期记账模板请求参数")
public class FinanceRecurringUpdateDto {

    @NotNull(message = "模板ID不能为空")
    @Schema(description = "模板ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Size(max = 64, message = "模板名称长度不能超过64个字符")
    @Schema(description = "模板名称", example = "工资")
    private String name;

    @Size(max = 16, message = "方向长度不能超过16个字符")
    @Schema(description = "方向：income/expense", example = "income")
    private String direction;

    @DecimalMin(value = "0.00", message = "金额不能为负数")
    @Schema(description = "默认金额（可为0，落账时再填）", example = "10000.00")
    private BigDecimal amount;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "账户ID", example = "1")
    private Long accountId;

    @Size(max = 16, message = "周期长度不能超过16个字符")
    @Schema(description = "周期：daily/weekly/monthly", example = "monthly")
    private String cycle;

    @Schema(description = "每月几号（monthly）", example = "15")
    private Integer dayOfMonth;

    @Schema(description = "星期几（weekly，1-7）", example = "1")
    private Integer weekday;

    @Schema(description = "是否启用：1-启用，0-禁用", example = "1")
    private Integer enabled;

    @Size(max = 512, message = "备注长度不能超过512个字符")
    @Schema(description = "备注")
    private String note;
}
