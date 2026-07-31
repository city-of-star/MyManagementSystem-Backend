package com.mms.base.common.finance.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 实现功能【记账初始化模板-快捷项实体】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("finance_tpl_recurring")
@Schema(description = "记账初始化模板-快捷项实体")
public class FinanceTplRecurringEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "模板名称")
    private String name;

    @Schema(description = "方向：income/expense/transfer")
    private String direction;

    @TableField(value = "category_id", updateStrategy = FieldStrategy.ALWAYS)
    @Schema(description = "模板分类ID")
    private Long categoryId;

    @TableField(value = "account_id", updateStrategy = FieldStrategy.ALWAYS)
    @Schema(description = "模板账户ID")
    private Long accountId;

    @TableField(value = "from_account_id", updateStrategy = FieldStrategy.ALWAYS)
    @Schema(description = "模板转出账户ID")
    private Long fromAccountId;

    @TableField(value = "to_account_id", updateStrategy = FieldStrategy.ALWAYS)
    @Schema(description = "模板转入账户ID")
    private Long toAccountId;

    @TableField("sort_order")
    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "是否启用：1-启用，0-禁用")
    private Integer enabled;

    @Schema(description = "备注")
    private String note;
}
