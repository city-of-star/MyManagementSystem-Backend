package com.mms.base.common.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 实现功能【基金赎回到账确认 DTO】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Data
@Schema(description = "将赎回 pending 转账改为 settled，可按实收微调金额")
public class FinanceFundSettleRedeemDto {

    @NotNull(message = "流水ID不能为空")
    @Schema(description = "赎回产生的转账流水ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long transactionId;

    @DecimalMin(value = "0.01", message = "实收金额必须大于0")
    @Schema(description = "实收到账金额；不填则沿用预估金额")
    private BigDecimal actualAmount;

    @Schema(description = "到账日期；不填则沿用原流水日期")
    private LocalDate settleDate;

    @Schema(description = "备注")
    private String note;
}
