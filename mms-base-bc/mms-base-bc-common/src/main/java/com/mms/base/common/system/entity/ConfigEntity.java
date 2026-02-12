package com.mms.base.common.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;

/**
 * 实现功能【系统配置实体】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-23 11:21:50
 */
@Data
@TableName("config")
@Schema(description = "系统配置实体")
public class ConfigEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("config_key")
    @Schema(description = "配置键（唯一标识）")
    private String configKey;

    @TableField("config_value")
    @Schema(description = "配置值")
    private String configValue;

    @TableField("config_type")
    @Schema(description = "配置类型：string-字符串，number-数字，boolean-布尔值，json-JSON对象")
    private String configType;

    @TableField("config_name")
    @Schema(description = "配置名称/描述")
    private String configName;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "是否可编辑：0-否（系统配置），1-是（用户配置）")
    private Integer editable;

    @Schema(description = "备注")
    private String remark;
}

