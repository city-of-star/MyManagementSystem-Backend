package com.mms.base.common.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 实现功能【由周期模板生成流水请求 DTO】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Data
@Schema(description = "由周期模板生成流水请求参数")
public class FinanceTransactionFromRecurringDto {

    @NotNull(message = "周期模板ID不能为空")
    @Schema(description = "周期模板ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long recurringId;

    @Schema(description = "交易日期，默认当天", example = "2026-07-30")
    private LocalDate txnDate;

    @DecimalMin(value = "0.01", message = "金额必须大于0")
    @Schema(description = "金额覆盖（可选）", example = "3000.00")
    private BigDecimal amount;

    @Size(max = 512, message = "备注长度不能超过512个字符")
    @Schema(description = "备注覆盖（可选）")
    private String note;
}
