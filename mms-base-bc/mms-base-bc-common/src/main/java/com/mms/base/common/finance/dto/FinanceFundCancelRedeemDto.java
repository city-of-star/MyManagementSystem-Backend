package com.mms.base.common.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 实现功能【撤销基金赎回 DTO】
 *
 * @author li.hongyu
 * @date 2026-08-01
 */
@Data
@Schema(description = "撤销待到账赎回：回滚持仓份额并关闭流水")
public class FinanceFundCancelRedeemDto {

    @NotNull(message = "流水ID不能为空")
    @Schema(description = "赎回产生的转账流水ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long transactionId;

    @Schema(description = "历史单无份额快照时：确认已手工加回持仓后，仅关闭流水")
    private Boolean forceClose;
}
