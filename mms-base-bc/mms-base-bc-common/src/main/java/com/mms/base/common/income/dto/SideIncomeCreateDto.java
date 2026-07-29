package com.mms.base.common.income.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 实现功能【创建副业收入记录请求 DTO】
 *
 * @author li.hongyu
 * @date 2026-07-29
 */
@Data
@Schema(description = "创建副业收入记录请求参数")
public class SideIncomeCreateDto {

    @NotNull(message = "业务日期不能为空")
    @Schema(description = "业务发生日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-07-29")
    private LocalDate recordDate;

    @NotNull(message = "应得金额不能为空")
    @DecimalMin(value = "0.01", message = "应得金额必须大于0")
    @Schema(description = "应得金额（元）", requiredMode = Schema.RequiredMode.REQUIRED, example = "50.00")
    private BigDecimal amount;

    @DecimalMin(value = "0.01", message = "整单流水必须大于0")
    @Schema(description = "整单流水（元），合作单建议填写", example = "100.00")
    private BigDecimal grossAmount;

    @NotBlank(message = "来源类型不能为空")
    @Size(max = 32, message = "来源类型长度不能超过32个字符")
    @Schema(description = "来源：self-自销，partner-合作分成，other-其他", requiredMode = Schema.RequiredMode.REQUIRED, example = "self")
    private String sourceType;

    @NotBlank(message = "状态不能为空")
    @Size(max = 32, message = "状态长度不能超过32个字符")
    @Schema(description = "状态：paid-已到账，pending-待结算", requiredMode = Schema.RequiredMode.REQUIRED, example = "paid")
    private String status;

    @Size(max = 512, message = "备注长度不能超过512个字符")
    @Schema(description = "备注", example = "微信导出工具第1单")
    private String note;
}
