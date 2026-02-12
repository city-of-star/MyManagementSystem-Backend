package com.mms.base.common.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;

/**
 * 实现功能【数据字典数据实体】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-23 11:21:50
 */
@Data
@TableName("dict_data")
@Schema(description = "数据字典数据实体")
public class DictDataEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("dict_type_id")
    @Schema(description = "字典类型ID")
    private Long dictTypeId;

    @TableField("dict_label")
    @Schema(description = "字典标签（显示文本）")
    private String dictLabel;

    @TableField("dict_value")
    @Schema(description = "字典值（实际值）")
    private String dictValue;

    @TableField("dict_sort")
    @Schema(description = "排序号")
    private Integer dictSort;

    @TableField("is_default")
    @Schema(description = "是否默认值：0-否，1-是")
    private Integer isDefault;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}

