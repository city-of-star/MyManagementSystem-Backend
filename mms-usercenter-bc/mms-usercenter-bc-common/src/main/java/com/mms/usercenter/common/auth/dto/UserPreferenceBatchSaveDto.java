package com.mms.usercenter.common.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 实现功能【批量保存用户偏好配置请求 DTO】
 * <p>
 * 用于一次保存多项偏好配置
 * </p>
 *
 * @author li.hongyu
 * @date 2026-04-27 15:00:00
 */
@Data
@Schema(description = "批量保存用户偏好配置请求参数")
public class UserPreferenceBatchSaveDto {

    @Valid
    @NotEmpty(message = "偏好配置列表不能为空")
    @Schema(description = "偏好配置列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<UserPreferenceSaveDto> preferences;
}
