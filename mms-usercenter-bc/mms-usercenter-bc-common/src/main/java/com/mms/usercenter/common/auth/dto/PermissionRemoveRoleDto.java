package com.mms.usercenter.common.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 实现功能【权限移除角色关联 DTO】
 *
 * 用于从权限中解除某个角色的关联关系
 */
@Data
@Schema(description = "权限移除角色请求参数")
public class PermissionRemoveRoleDto {

    @NotNull(message = "权限ID不能为空")
    @Schema(description = "权限ID", example = "1")
    private Long permissionId;

    @NotNull(message = "角色ID不能为空")
    @Schema(description = "角色ID", example = "1")
    private Long roleId;
}

