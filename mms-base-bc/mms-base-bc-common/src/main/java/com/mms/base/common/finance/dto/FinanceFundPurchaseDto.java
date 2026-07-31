package com.mms.base.common.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 实现功能【基金申购请求 DTO】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Data
@Schema(description = "基金申购：增加份额并按移动平均更新成本；可选生成银行卡→基金壳已入账转账")
public class FinanceFundPurchaseDto {

    @NotNull(message = "持仓ID不能为空")
    @Schema(description = "持仓ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long holdingId;

    @NotNull(message = "申购份额不能为空")
    @DecimalMin(value = "0.000001", message = "申购份额必须大于0")
    @Schema(description = "申购份额", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal shares;

    @NotNull(message = "申购金额不能为空")
    @DecimalMin(value = "0.01", message = "申购金额必须大于0")
    @Schema(description = "申购金额（计入成本）", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @Schema(description = "业务日期，默认今天")
    private LocalDate txnDate;

    @Schema(description = "扣款账户ID；填写则生成 settled 转账到基金壳")
    private Long fromAccountId;

    @Schema(description = "申购后净值（可选，用于刷新市值）")
    private BigDecimal nav;

    @Schema(description = "净值日期")
    private LocalDate navDate;

    @Schema(description = "备注")
    private String note;
}
