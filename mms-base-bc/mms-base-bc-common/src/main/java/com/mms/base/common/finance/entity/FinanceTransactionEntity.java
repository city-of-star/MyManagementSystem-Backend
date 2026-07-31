package com.mms.base.common.finance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 实现功能【记账流水实体】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("finance_transaction")
@Schema(description = "记账流水实体")
public class FinanceTransactionEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("user_id")
    @Schema(description = "归属用户ID")
    private Long userId;

    @TableField("txn_date")
    @Schema(description = "交易日期")
    private LocalDate txnDate;

    @TableField("txn_type")
    @Schema(description = "交易类型：income/expense/transfer")
    private String txnType;

    @Schema(description = "金额")
    private BigDecimal amount;

    @TableField("category_id")
    @Schema(description = "分类ID")
    private Long categoryId;

    @TableField("account_id")
    @Schema(description = "账户ID（收入/支出）")
    private Long accountId;

    @TableField("from_account_id")
    @Schema(description = "转出账户ID（转账）")
    private Long fromAccountId;

    @TableField("to_account_id")
    @Schema(description = "转入账户ID（转账）")
    private Long toAccountId;

    @Schema(description = "状态：settled-已入账，pending-待入账")
    private String status;

    @Schema(description = "备注")
    private String note;
}
