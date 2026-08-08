package com.mms.base.common.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 实现功能【工资条批量入账明细行 DTO】
 *
 * @author li.hongyu
 * @date 2026-08-08
 */
@Data
@Schema(description = "工资条批量入账明细行")
public class FinancePayrollBatchLineDto {

    @NotBlank(message = "明细名称不能为空")
    @Size(max = 64, message = "明细名称长度不能超过64")
    @Schema(description = "展示名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String label;

    @NotBlank(message = "明细类型不能为空")
    @Schema(description = "income/expense/transfer", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lineType;

    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0", message = "金额不能为负")
    @Schema(description = "金额；大于 0 才入账", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @Schema(description = "分类ID（收入/支出必填）")
    private Long categoryId;

    @Schema(description = "账户ID（收入/支出必填）")
    private Long accountId;

    @Schema(description = "转出账户ID（转账必填）")
    private Long fromAccountId;

    @Schema(description = "转入账户ID（转账必填）")
    private Long toAccountId;
}
