package com.mms.base.common.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 实现功能【更新系统配置请求 DTO】
 * <p>
 * 用于更新系统配置信息的请求参数
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-23 11:21:50
 */
@Data
@Schema(description = "更新系统配置请求参数")
public class ConfigUpdateDto {

    @NotNull(message = "配置ID不能为空")
    @Schema(description = "配置ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "配置值", example = "MyManagementSystem")
    private String configValue;

    @Size(max = 32, message = "配置类型长度不能超过32个字符")
    @Schema(description = "配置类型：string-字符串，number-数字，boolean-布尔值，json-JSON对象", example = "string")
    private String configType;

    @Size(max = 128, message = "配置名称长度不能超过128个字符")
    @Schema(description = "配置名称/描述", example = "系统名称")
    private String configName;

    @Schema(description = "状态：0-禁用，1-启用", example = "1")
    private Integer status;

    @Schema(description = "是否可编辑：0-否（系统配置），1-是（用户配置）", example = "1")
    private Integer editable;

    @Schema(description = "备注", example = "系统名称配置")
    private String remark;
}

