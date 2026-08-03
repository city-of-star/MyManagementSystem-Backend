package com.mms.base.common.finance.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * 实现功能【快捷记账模板实体】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("finance_recurring")
@Schema(description = "快捷记账模板实体")
public class FinanceRecurringEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("user_id")
    @Schema(description = "归属用户ID")
    private Long userId;

    @Schema(description = "模板名称")
    private String name;

    @Schema(description = "方向：income/expense/transfer")
    private String direction;

    @Schema(description = "金额")
    private BigDecimal amount;

    @TableField(value = "category_id", updateStrategy = FieldStrategy.ALWAYS)
    @Schema(description = "分类ID（收入/支出必填，转账可空）")
    private Long categoryId;

    @TableField(value = "account_id", updateStrategy = FieldStrategy.ALWAYS)
    @Schema(description = "账户ID（收入/支出必填，转账可空）")
    private Long accountId;

    @TableField(value = "from_account_id", updateStrategy = FieldStrategy.ALWAYS)
    @Schema(description = "转出账户ID（转账模板）")
    private Long fromAccountId;

    @TableField(value = "to_account_id", updateStrategy = FieldStrategy.ALWAYS)
    @Schema(description = "转入账户ID（转账模板）")
    private Long toAccountId;

    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    @Schema(description = "周期：none/daily/weekly/monthly")
    private String cycle;

    @TableField(value = "day_of_month", updateStrategy = FieldStrategy.ALWAYS)
    @Schema(description = "每月几号（cycle=monthly）")
    private Integer dayOfMonth;

    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    @Schema(description = "星期几（cycle=weekly，1=周一 … 7=周日）")
    private Integer weekday;

    @TableField("sort_order")
    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "是否启用：1-启用，0-禁用")
    private Integer enabled;

    @Schema(description = "备注")
    private String note;
}
