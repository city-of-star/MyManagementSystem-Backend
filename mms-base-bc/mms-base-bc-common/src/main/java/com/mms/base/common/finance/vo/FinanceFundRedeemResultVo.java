package com.mms.base.common.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 实现功能【基金赎回结果 VO】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Data
@Schema(description = "基金赎回结果")
public class FinanceFundRedeemResultVo {

    @Schema(description = "更新后的持仓")
    private FinanceFundHoldingVo holding;

    @Schema(description = "pending 转账流水ID（到账后 settle）")
    private Long pendingTransactionId;
}
