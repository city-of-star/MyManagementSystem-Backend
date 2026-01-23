package com.mms.usercenter.common.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 实现功能【用户分配部门 DTO】
 *
 * @author li.hongyu
 * @date 2026-01-23 14:20:15
 */
@Data
@Schema(description = "用户分配部门请求参数")
public class UserAssignDeptDto {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @NotEmpty(message = "部门ID列表不能为空")
    @Schema(description = "部门ID列表", example = "[1, 2, 3]")
    private List<Long> deptIds;

    @Schema(description = "主部门ID，必须包含在部门ID列表中", example = "1")
    private Long primaryDeptId;
}

