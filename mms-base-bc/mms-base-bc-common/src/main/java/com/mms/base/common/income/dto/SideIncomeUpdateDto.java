package com.mms.base.common.income.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 实现功能【更新副业收入记录请求 DTO】
 *
 * @author li.hongyu
 * @date 2026-07-29
 */
@Data
@Schema(description = "更新副业收入记录请求参数")
public class SideIncomeUpdateDto {

    @NotNull(message = "记录ID不能为空")
    @Schema(description = "记录ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "业务发生日期", example = "2026-07-29")
    private LocalDate recordDate;

    @DecimalMin(value = "0.01", message = "应得金额必须大于0")
    @Schema(description = "应得金额（元）", example = "50.00")
    private BigDecimal amount;

    @DecimalMin(value = "0.01", message = "整单流水必须大于0")
    @Schema(description = "整单流水（元）", example = "100.00")
    private BigDecimal grossAmount;

    @Size(max = 32, message = "来源类型长度不能超过32个字符")
    @Schema(description = "来源：self-自销，partner-合作分成，other-其他", example = "partner")
    private String sourceType;

    @Size(max = 32, message = "状态长度不能超过32个字符")
    @Schema(description = "状态：paid-已到账，pending-待结算", example = "pending")
    private String status;

    @Size(max = 512, message = "备注长度不能超过512个字符")
    @Schema(description = "备注")
    private String note;
}
