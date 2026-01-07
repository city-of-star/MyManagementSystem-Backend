package com.mms.usercenter.common.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 实现功能【权限创建 DTO】
 *
 * @author li.hongyu
 * @date 2025-12-22 12:00:00
 */
@Data
@Schema(description = "权限创建请求参数")
public class PermissionCreateDto {

    @Schema(description = "父权限ID，0表示顶级权限", example = "0")
    private Long parentId = 0L;

    @NotBlank(message = "权限类型不能为空")
    @Schema(description = "权限类型：catalog-目录，menu-菜单，button-按钮，api-接口", example = "menu")
    private String permissionType;

    @NotBlank(message = "权限名称不能为空")
    @Schema(description = "权限名称", example = "用户管理")
    private String permissionName;

    @NotBlank(message = "权限编码不能为空")
    @Schema(description = "权限编码（唯一标识）", example = "user:manage")
    private String permissionCode;

    @Schema(description = "路由路径（菜单类型）", example = "/user")
    private String path;

    @Schema(description = "组件路径（菜单类型）", example = "user/index")
    private String component;

    @Schema(description = "图标（菜单类型）", example = "user")
    private String icon;

    @Schema(description = "接口URL（接口类型）", example = "/api/user/list")
    private String apiUrl;

    @Schema(description = "接口请求方式：GET,POST,PUT,DELETE", example = "GET")
    private String apiMethod;

    @Schema(description = "排序号", example = "1")
    private Integer sortOrder;

    @Schema(description = "是否显示：0-隐藏，1-显示", example = "1")
    private Integer visible;

    @Schema(description = "状态：0-禁用，1-启用", example = "1")
    private Integer status;

    @Schema(description = "备注", example = "权限说明")
    private String remark;
}

