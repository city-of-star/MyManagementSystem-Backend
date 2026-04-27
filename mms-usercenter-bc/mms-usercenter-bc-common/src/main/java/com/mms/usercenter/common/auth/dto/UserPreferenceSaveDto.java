package com.mms.usercenter.common.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 实现功能【保存用户偏好配置请求 DTO】
 * <p>
 * 用于保存单个偏好配置
 * </p>
 *
 * @author li.hongyu
 * @date 2026-04-27 15:00:00
 */
@Data
@Schema(description = "保存用户偏好配置请求参数")
public class UserPreferenceSaveDto {

    @NotBlank(message = "偏好键不能为空")
    @Size(max = 128, message = "偏好键长度不能超过128个字符")
    @Schema(description = "偏好键，如 theme.color / layout.mode", requiredMode = Schema.RequiredMode.REQUIRED, example = "theme.color")
    private String prefKey;

    @Schema(description = "偏好值", example = "#1677ff")
    private String prefValue;

    @NotBlank(message = "值类型不能为空")
    @Size(max = 16, message = "值类型长度不能超过16个字符")
    @Pattern(regexp = "^(?i)(string|number|boolean|json)$", message = "值类型仅支持 string/number/boolean/json")
    @Schema(description = "值类型：string/number/boolean/json", requiredMode = Schema.RequiredMode.REQUIRED, example = "string")
    private String valueType = "string";

    @Size(max = 512, message = "备注长度不能超过512个字符")
    @Schema(description = "备注", example = "主题色偏好")
    private String remark;
}
