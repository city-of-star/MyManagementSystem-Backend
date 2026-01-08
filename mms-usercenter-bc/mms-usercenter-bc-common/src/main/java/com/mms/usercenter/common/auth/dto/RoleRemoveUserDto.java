package com.mms.usercenter.common.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 实现功能【角色移除用户 DTO】
 *
 * @author li.hongyu
 * @date 2025-12-22 11:00:00
 */
@Data
@Schema(description = "角色移除用户请求参数")
public class RoleRemoveUserDto {

    @NotNull(message = "角色ID不能为空")
    @Schema(description = "角色ID", example = "1")
    private Long roleId;

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", example = "1")
    private Long userId;
}

