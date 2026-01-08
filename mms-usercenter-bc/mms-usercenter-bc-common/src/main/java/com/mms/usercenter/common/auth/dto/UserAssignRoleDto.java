package com.mms.usercenter.common.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 实现功能【用户分配角色 DTO】
 *
 * @author li.hongyu
 * @date 2025-12-22 11:00:00
 */
@Data
@Schema(description = "用户分配角色请求参数")
public class UserAssignRoleDto {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @NotEmpty(message = "角色ID列表不能为空")
    @Schema(description = "角色ID列表", example = "[1, 2, 3]")
    private List<Long> roleIds;
}

