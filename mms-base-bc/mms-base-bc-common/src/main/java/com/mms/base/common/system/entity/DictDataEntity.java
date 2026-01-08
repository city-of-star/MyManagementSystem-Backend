package com.mms.base.common.system.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

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
public class DictDataEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "字典数据ID")
    private Long id;

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

    @TableLogic(value = "0", delval = "1")
    @Schema(description = "逻辑删除标记：0-未删除，1-已删除")
    private Integer deleted;

    @TableField(value = "create_by", fill = FieldFill.INSERT)
    @Schema(description = "创建人ID")
    private Long createBy;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新人ID")
    private Long updateBy;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}

