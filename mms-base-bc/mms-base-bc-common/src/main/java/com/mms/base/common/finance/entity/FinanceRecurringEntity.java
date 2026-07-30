package com.mms.base.common.finance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * 实现功能【周期记账模板实体】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("finance_recurring")
@Schema(description = "周期记账模板实体")
public class FinanceRecurringEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "模板名称")
    private String name;

    @Schema(description = "方向：income-收入，expense-支出")
    private String direction;

    @Schema(description = "金额")
    private BigDecimal amount;

    @TableField("category_id")
    @Schema(description = "分类ID")
    private Long categoryId;

    @TableField("account_id")
    @Schema(description = "账户ID")
    private Long accountId;

    @Schema(description = "周期：daily/weekly/monthly")
    private String cycle;

    @TableField("day_of_month")
    @Schema(description = "每月几号（monthly）")
    private Integer dayOfMonth;

    @Schema(description = "星期几（weekly，1-7）")
    private Integer weekday;

    @Schema(description = "是否启用：1-启用，0-禁用")
    private Integer enabled;

    @Schema(description = "备注")
    private String note;
}
