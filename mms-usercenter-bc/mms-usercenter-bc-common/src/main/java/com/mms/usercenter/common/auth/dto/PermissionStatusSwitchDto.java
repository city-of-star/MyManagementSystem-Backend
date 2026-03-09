package com.mms.usercenter.common.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * 实现功能【权限状态切换 DTO】
 *
 * @author li.hongyu
 * @date 2025-12-22 12:00:00
 */
@Data
@Schema(description = "权限状态切换请求参数")
public class PermissionStatusSwitchDto {

    @NotNull(message = "权限ID不能为空")
    @Schema(description = "权限ID", example = "1")
    private Long permissionId;

    @NotNull(message = "状态不能为空")
    @Range(min = 0, max = 1, message = "状态值只能是0或1")
    @Schema(description = "状态：0-禁用，1-启用", example = "1")
    private Integer status;
}

