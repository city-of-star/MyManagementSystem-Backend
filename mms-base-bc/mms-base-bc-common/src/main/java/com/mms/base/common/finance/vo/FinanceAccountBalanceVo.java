package com.mms.base.common.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 实现功能【账户余额响应 VO】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Data
@Schema(description = "账户余额响应对象")
public class FinanceAccountBalanceVo {

    @Schema(description = "账户ID")
    private Long accountId;

    @Schema(description = "账户名称")
    private String accountName;

    @Schema(description = "账户类型")
    private String accountType;

    @Schema(description = "当前余额")
    private BigDecimal balance;
}
