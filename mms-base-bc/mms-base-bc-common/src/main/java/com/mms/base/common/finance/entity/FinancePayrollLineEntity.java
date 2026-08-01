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
 * 实现功能【个人工资录入明细行配置】
 *
 * @author li.hongyu
 * @date 2026-08-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("finance_payroll_line")
@Schema(description = "个人工资录入明细行配置")
public class FinancePayrollLineEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("user_id")
    @Schema(description = "归属用户ID")
    private Long userId;

    @TableField("profile_id")
    @Schema(description = "配置头ID")
    private Long profileId;

    @TableField("line_key")
    @Schema(description = "稳定键")
    private String lineKey;

    @Schema(description = "展示名称")
    private String label;

    @TableField("line_type")
    @Schema(description = "income/expense/transfer")
    private String lineType;

    @TableField("category_id")
    @Schema(description = "分类ID")
    private Long categoryId;

    @TableField("account_id")
    @Schema(description = "账户ID")
    private Long accountId;

    @TableField("from_account_id")
    @Schema(description = "转出账户ID")
    private Long fromAccountId;

    @TableField("to_account_id")
    @Schema(description = "转入账户ID")
    private Long toAccountId;

    @TableField("count_in_net")
    @Schema(description = "是否计入预估到手")
    private Integer countInNet;

    @TableField("default_amount")
    @Schema(description = "默认金额")
    private BigDecimal defaultAmount;

    @TableField("sort_order")
    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "是否启用")
    private Integer enabled;
}
