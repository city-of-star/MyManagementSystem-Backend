package com.mms.base.common.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 实现功能【平账请求 DTO】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Data
@Schema(description = "平账请求：按真实余额生成差额流水")
public class FinanceAdjustmentDto {

    @NotNull(message = "账户不能为空")
    @Schema(description = "账户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long accountId;

    @NotNull(message = "真实余额不能为空")
    @Schema(description = "当前真实余额", requiredMode = Schema.RequiredMode.REQUIRED, example = "1234.56")
    private BigDecimal actualBalance;

    @NotNull(message = "交易日期不能为空")
    @Schema(description = "交易日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-07-31")
    private LocalDate txnDate;

    @Size(max = 512, message = "备注长度不能超过512个字符")
    @Schema(description = "备注")
    private String note;
}
