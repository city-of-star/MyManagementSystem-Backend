package com.mms.base.common.finance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 实现功能【记账初始化模板-分类实体】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("finance_tpl_category")
@Schema(description = "记账初始化模板-分类实体")
public class FinanceTplCategoryEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "方向：income/expense")
    private String direction;

    @Schema(description = "图标")
    private String icon;

    @TableField("sort_order")
    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "是否启用：1-启用，0-禁用")
    private Integer enabled;
}
