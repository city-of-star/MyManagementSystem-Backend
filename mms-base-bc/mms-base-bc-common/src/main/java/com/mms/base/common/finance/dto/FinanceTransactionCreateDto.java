package com.mms.base.common.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 实现功能【创建记账流水请求 DTO】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Data
@Schema(description = "创建记账流水请求参数")
public class FinanceTransactionCreateDto {

    @NotNull(message = "交易日期不能为空")
    @Schema(description = "交易日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-07-30")
    private LocalDate txnDate;

    @NotBlank(message = "交易类型不能为空")
    @Size(max = 16, message = "交易类型长度不能超过16个字符")
    @Schema(description = "交易类型：income/expense/transfer", requiredMode = Schema.RequiredMode.REQUIRED, example = "expense")
    private String txnType;

    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    @Schema(description = "金额", requiredMode = Schema.RequiredMode.REQUIRED, example = "28.50")
    private BigDecimal amount;

    @Schema(description = "分类ID（收入/支出必填，转账可选）")
    private Long categoryId;

    @Schema(description = "账户ID（收入/支出）")
    private Long accountId;

    @Schema(description = "转出账户ID（转账）")
    private Long fromAccountId;

    @Schema(description = "转入账户ID（转账）")
    private Long toAccountId;

    @Size(max = 16, message = "状态长度不能超过16个字符")
    @Schema(description = "状态：settled/pending，默认 settled", example = "settled")
    private String status;

    @Size(max = 512, message = "备注长度不能超过512个字符")
    @Schema(description = "备注")
    private String note;
}
