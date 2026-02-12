package com.mms.base.common.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
/**
 * 实现功能【数据字典类型实体】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-23 11:21:50
 */
@Data
@TableName("dict_type")
@Schema(description = "数据字典类型实体")
public class DictTypeEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("dict_type_code")
    @Schema(description = "字典类型编码（唯一标识）")
    private String dictTypeCode;

    @TableField("dict_type_name")
    @Schema(description = "字典类型名称")
    private String dictTypeName;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @TableField("sort_order")
    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "备注")
    private String remark;
}

