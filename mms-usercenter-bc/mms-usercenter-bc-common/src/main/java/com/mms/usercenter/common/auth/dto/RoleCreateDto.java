package com.mms.usercenter.common.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 实现功能【角色创建 DTO】
 *
 * @author li.hongyu
 * @date 2025-12-22 11:00:00
 */
@Data
@Schema(description = "角色创建请求参数")
public class RoleCreateDto {

    @NotBlank(message = "角色编码不能为空")
    @Schema(description = "角色编码", example = "admin")
    private String roleCode;

    @NotBlank(message = "角色名称不能为空")
    @Schema(description = "角色名称", example = "超级管理员")
    private String roleName;

    @Schema(description = "角色类型：system-系统角色，custom-自定义角色", example = "custom")
    private String roleType;

    @Schema(description = "排序号", example = "1")
    private Integer sortOrder;

    @Schema(description = "状态：0-禁用，1-启用", example = "1")
    private Integer status;

    @Schema(description = "备注", example = "系统内置角色")
    private String remark;
}

