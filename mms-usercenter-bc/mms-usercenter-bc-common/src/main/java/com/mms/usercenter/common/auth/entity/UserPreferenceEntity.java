package com.mms.usercenter.common.auth.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;

/**
 * 实现功能【用户偏好配置实体】
 * <p>
 * 存储用户个性化偏好配置（按偏好键存储）
 * </p>
 *
 * @author li.hongyu
 * @date 2026-04-27 15:00:00
 */
@Data
@TableName("system_user_preference")
@Schema(description = "用户偏好配置实体")
public class UserPreferenceEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("user_id")
    @Schema(description = "用户ID")
    private Long userId;

    @TableField("pref_key")
    @Schema(description = "偏好键")
    private String prefKey;

    @TableField("pref_value")
    @Schema(description = "偏好值")
    private String prefValue;

    @TableField("value_type")
    @Schema(description = "值类型：string/number/boolean/json")
    private String valueType;

    @Schema(description = "备注")
    private String remark;
}
