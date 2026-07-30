package com.mms.base.common.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 实现功能【记账流水响应 VO】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Data
@Schema(description = "记账流水响应对象")
public class FinanceTransactionVo {

    @Schema(description = "流水ID")
    private Long id;

    @Schema(description = "交易日期")
    private LocalDate txnDate;

    @Schema(description = "交易类型：income/expense/transfer")
    private String txnType;

    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "账户ID")
    private Long accountId;

    @Schema(description = "账户名称")
    private String accountName;

    @Schema(description = "转出账户ID")
    private Long fromAccountId;

    @Schema(description = "转出账户名称")
    private String fromAccountName;

    @Schema(description = "转入账户ID")
    private Long toAccountId;

    @Schema(description = "转入账户名称")
    private String toAccountName;

    @Schema(description = "状态：settled/pending")
    private String status;

    @Schema(description = "备注")
    private String note;

    @Schema(description = "创建人ID")
    private Long createBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新人ID")
    private Long updateBy;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
