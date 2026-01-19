package com.mms.usercenter.controller.auth;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.security.annotations.RequiresPermission;
import com.mms.common.core.response.Response;
import com.mms.usercenter.common.auth.dto.PermissionBatchDeleteDto;
import com.mms.usercenter.common.auth.dto.PermissionCreateDto;
import com.mms.usercenter.common.auth.dto.PermissionPageQueryDto;
import com.mms.usercenter.common.auth.dto.PermissionRemoveRoleDto;
import com.mms.usercenter.common.auth.dto.PermissionStatusSwitchDto;
import com.mms.usercenter.common.auth.dto.PermissionUpdateDto;
import com.mms.usercenter.common.auth.vo.PermissionVo;
import com.mms.usercenter.common.auth.vo.RoleVo;
import com.mms.common.core.constants.usercenter.PermissionConstants;
import com.mms.usercenter.service.auth.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 实现功能【权限/菜单管理 Controller】
 * <p>
 * 提供权限与菜单管理的REST API接口
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-12 09:37:10
 */
@Tag(name = "权限管理", description = "权限/菜单相关接口")
@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Resource
    private PermissionService permissionService;

    @Operation(summary = "分页查询权限列表")
    @RequiresPermission(PermissionConstants.PERMISSION_VIEW)
    @PostMapping("/page")
    public Response<Page<PermissionVo>> getPermissionPage(@RequestBody @Valid PermissionPageQueryDto dto) {
        return Response.success(permissionService.getPermissionPage(dto));
    }

    @Operation(summary = "根据ID查询权限详情")
    @RequiresPermission(PermissionConstants.PERMISSION_VIEW)
    @GetMapping("/{permissionId}")
    public Response<PermissionVo> getPermissionById(@PathVariable Long permissionId) {
        return Response.success(permissionService.getPermissionById(permissionId));
    }

    @Operation(summary = "创建权限")
    @RequiresPermission(PermissionConstants.PERMISSION_CREATE)
    @PostMapping("/create")
    public Response<PermissionVo> createPermission(@RequestBody @Valid PermissionCreateDto dto) {
        return Response.success(permissionService.createPermission(dto));
    }

    @Operation(summary = "更新权限")
    @RequiresPermission(PermissionConstants.PERMISSION_UPDATE)
    @PutMapping("/update")
    public Response<PermissionVo> updatePermission(@RequestBody @Valid PermissionUpdateDto dto) {
        return Response.success(permissionService.updatePermission(dto));
    }

    @Operation(summary = "删除权限")
    @RequiresPermission(PermissionConstants.PERMISSION_DELETE)
    @DeleteMapping("/{permissionId}")
    public Response<Void> deletePermission(@PathVariable Long permissionId) {
        permissionService.deletePermission(permissionId);
        return Response.success();
    }

    @Operation(summary = "批量删除权限")
    @RequiresPermission(PermissionConstants.PERMISSION_DELETE)
    @PostMapping("/batch-delete")
    public Response<Void> batchDeletePermission(@RequestBody @Valid PermissionBatchDeleteDto dto) {
        permissionService.batchDeletePermission(dto);
        return Response.success();
    }

    @Operation(summary = "切换权限状态（启用/禁用）")
    @RequiresPermission(PermissionConstants.PERMISSION_UPDATE)
    @PostMapping("/switch-status")
    public Response<Void> switchPermissionStatus(@RequestBody @Valid PermissionStatusSwitchDto dto) {
        permissionService.switchPermissionStatus(dto);
        return Response.success();
    }

    @Operation(summary = "查询权限树（全量，用于管理场景）")
    @RequiresPermission(PermissionConstants.PERMISSION_VIEW)
    @GetMapping("/tree")
    public Response<List<PermissionVo>> listPermissionTree(@RequestParam(required = false) String permissionType,
                                                           @RequestParam(required = false) Integer status,
                                                           @RequestParam(required = false) Integer visible) {
        return Response.success(permissionService.listPermissionTree(permissionType, status, visible));
    }

    @Operation(summary = "查询当前用户的权限树（用于前端菜单展示）", 
               description = "返回当前登录用户有权限的权限树，固定返回启用、可见、目录或菜单类型的权限")
    @GetMapping("/tree/current-user")
    public Response<List<PermissionVo>> listCurrentUserPermissionTree() {
        return Response.success(permissionService.listCurrentUserPermissionTree());
    }

    @Operation(summary = "查询权限关联的角色列表")
    @RequiresPermission(PermissionConstants.PERMISSION_VIEW)
    @GetMapping("/{permissionId}/roles")
    public Response<List<RoleVo>> listRolesByPermissionId(@PathVariable Long permissionId) {
        return Response.success(permissionService.listRolesByPermissionId(permissionId));
    }

    @Operation(summary = "移除权限与角色的关联")
    @RequiresPermission(PermissionConstants.PERMISSION_UPDATE)
    @PostMapping("/remove-role")
    public Response<Void> removeRoleFromPermission(@RequestBody @Valid PermissionRemoveRoleDto dto) {
        permissionService.removeRoleFromPermission(dto);
        return Response.success();
    }
}