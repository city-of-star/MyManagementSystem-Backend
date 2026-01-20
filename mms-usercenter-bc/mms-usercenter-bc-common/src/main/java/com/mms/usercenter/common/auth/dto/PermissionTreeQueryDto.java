package com.mms.usercenter.common.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
/**
 * 实现功能【权限树查询 DTO】
 * <p>
 * 用于查询全量权限树的过滤条件（非分页）
 * <p>
 *
 * @author li.hongyu
 * @date 2026-01-20 17:41:23
 */
@Data
@Schema(description = "权限树查询请求参数")
public class PermissionTreeQueryDto {

    @Schema(description = "权限名称（模糊查询）", example = "用户")
    private String permissionName;

    @Schema(description = "权限编码（模糊查询）", example = "user")
    private String permissionCode;

    @Schema(description = "权限类型：catalog/menu/button/api", example = "menu")
    private String permissionType;

    @Schema(description = "状态：0-禁用，1-启用", example = "1")
    private Integer status;

    @Schema(description = "显示状态：0-隐藏，1-显示", example = "1")
    private Integer visible;
}
