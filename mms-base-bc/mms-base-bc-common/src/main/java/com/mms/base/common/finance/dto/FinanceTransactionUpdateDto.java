package com.mms.base.common.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 实现功能【更新记账流水请求 DTO】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Data
@Schema(description = "更新记账流水请求参数")
public class FinanceTransactionUpdateDto {

    @NotNull(message = "流水ID不能为空")
    @Schema(description = "流水ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "交易日期", example = "2026-07-30")
    private LocalDate txnDate;

    @Size(max = 16, message = "交易类型长度不能超过16个字符")
    @Schema(description = "交易类型：income/expense/transfer", example = "expense")
    private String txnType;

    @Schema(description = "金额（收入/支出/转账须大于0；平账可为负）", example = "28.50")
    private BigDecimal amount;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "账户ID")
    private Long accountId;

    @Schema(description = "转出账户ID")
    private Long fromAccountId;

    @Schema(description = "转入账户ID")
    private Long toAccountId;

    @Size(max = 16, message = "状态长度不能超过16个字符")
    @Schema(description = "状态：settled/pending", example = "settled")
    private String status;

    @Size(max = 512, message = "备注长度不能超过512个字符")
    @Schema(description = "备注")
    private String note;
}
