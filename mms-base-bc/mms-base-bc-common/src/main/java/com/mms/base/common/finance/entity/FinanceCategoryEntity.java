package com.mms.base.common.finance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 实现功能【记账分类实体】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("finance_category")
@Schema(description = "记账分类实体")
public class FinanceCategoryEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "方向：income-收入，expense-支出")
    private String direction;

    @Schema(description = "图标")
    private String icon;

    @TableField("sort_order")
    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "是否启用：1-启用，0-禁用")
    private Integer enabled;

    @TableField("is_system")
    @Schema(description = "是否系统内置：1-是，0-否")
    private Integer isSystem;
}
