package com.mms.base.common.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 实现功能【基金赎回请求 DTO】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Data
@Schema(description = "基金赎回：扣减份额并生成基金壳→到账账户的 pending 转账")
public class FinanceFundRedeemDto {

    @NotNull(message = "持仓ID不能为空")
    @Schema(description = "持仓ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long holdingId;

    @NotNull(message = "赎回份额不能为空")
    @DecimalMin(value = "0.000001", message = "赎回份额必须大于0")
    @Schema(description = "赎回份额", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal shares;

    @NotNull(message = "预估到账金额不能为空")
    @DecimalMin(value = "0.01", message = "预估到账金额必须大于0")
    @Schema(description = "预估到账金额", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @NotNull(message = "到账账户不能为空")
    @Schema(description = "到账账户ID（银行卡等）", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long toAccountId;

    @Schema(description = "业务日期，默认今天")
    private LocalDate txnDate;

    @Schema(description = "备注")
    private String note;
}
