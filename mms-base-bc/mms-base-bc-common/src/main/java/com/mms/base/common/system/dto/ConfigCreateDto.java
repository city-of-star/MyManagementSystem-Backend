package com.mms.base.common.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * 实现功能【创建系统配置请求 DTO】
 * <p>
 * 用于创建新系统配置的请求参数
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-23 11:21:50
 */
@Data
@Schema(description = "创建系统配置请求参数")
public class ConfigCreateDto {

    @NotBlank(message = "配置键不能为空")
    @Size(max = 128, message = "配置键长度不能超过128个字符")
    @Schema(description = "配置键（唯一标识）", requiredMode = Schema.RequiredMode.REQUIRED, example = "system.name")
    private String configKey;

    @Schema(description = "配置值", example = "MyManagementSystem")
    private String configValue;

    @NotBlank(message = "配置类型不能为空")
    @Size(max = 32, message = "配置类型长度不能超过32个字符")
    @Schema(description = "配置类型：string-字符串，number-数字，boolean-布尔值，json-JSON对象", requiredMode = Schema.RequiredMode.REQUIRED, example = "string")
    private String configType = "string";

    @NotBlank(message = "配置名称不能为空")
    @Size(max = 128, message = "配置名称长度不能超过128个字符")
    @Schema(description = "配置名称/描述", requiredMode = Schema.RequiredMode.REQUIRED, example = "系统名称")
    private String configName;

    @Range(min = 0, max = 1, message = "状态值只能是0或1")
    @Schema(description = "状态：0-禁用，1-启用，默认为1", example = "1")
    private Integer status = 1;

    @Range(min = 0, max = 1, message = "编辑值只能是0或1")
    @Schema(description = "是否可编辑：0-否（系统配置），1-是（用户配置），默认为1", example = "1")
    private Integer editable = 1;

    @Schema(description = "备注", example = "系统名称配置")
    private String remark;
}

