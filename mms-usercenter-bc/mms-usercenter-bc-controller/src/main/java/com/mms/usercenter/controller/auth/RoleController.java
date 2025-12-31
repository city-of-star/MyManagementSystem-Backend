package com.mms.usercenter.controller.auth;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.security.annotations.RequiresPermission;
import com.mms.common.core.response.Response;
import com.mms.usercenter.common.auth.dto.RoleAssignPermissionDto;
import com.mms.usercenter.common.auth.dto.RoleBatchDeleteDto;
import com.mms.usercenter.common.auth.dto.RoleCreateDto;
import com.mms.usercenter.common.auth.dto.RolePageQueryDto;
import com.mms.usercenter.common.auth.dto.RoleStatusSwitchDto;
import com.mms.usercenter.common.auth.dto.RoleUpdateDto;
import com.mms.usercenter.common.auth.vo.RoleVo;
import com.mms.common.core.constants.usercenter.PermissionConstants;
import com.mms.usercenter.service.auth.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 实现功能【角色服务 Controller】
 * <p>
 * 提供角色管理的REST API接口
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-12 09:36:59
 */
@Tag(name = "角色服务", description = "角色服务相关接口")
@RestController
@RequestMapping("/role")
public class RoleController {

    @Resource
    private RoleService roleService;

    @Operation(summary = "分页查询角色列表")
    @RequiresPermission(PermissionConstants.ROLE_VIEW)
    @PostMapping("/page")
    public Response<Page<RoleVo>> getRolePage(@RequestBody @Valid RolePageQueryDto dto) {
        return Response.success(roleService.getRolePage(dto));
    }

    @Operation(summary = "根据ID查询角色详情")
    @RequiresPermission(PermissionConstants.ROLE_VIEW)
    @GetMapping("/{roleId}")
    public Response<RoleVo> getRoleById(@PathVariable Long roleId) {
        return Response.success(roleService.getRoleById(roleId));
    }

    @Operation(summary = "创建角色")
    @RequiresPermission(PermissionConstants.ROLE_CREATE)
    @PostMapping("/create")
    public Response<RoleVo> createRole(@RequestBody @Valid RoleCreateDto dto) {
        return Response.success(roleService.createRole(dto));
    }

    @Operation(summary = "更新角色")
    @RequiresPermission(PermissionConstants.ROLE_UPDATE)
    @PutMapping("/update")
    public Response<RoleVo> updateRole(@RequestBody @Valid RoleUpdateDto dto) {
        return Response.success(roleService.updateRole(dto));
    }

    @Operation(summary = "删除角色")
    @RequiresPermission(PermissionConstants.ROLE_DELETE)
    @DeleteMapping("/{roleId}")
    public Response<Void> deleteRole(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return Response.success();
    }

    @Operation(summary = "批量删除角色")
    @RequiresPermission(PermissionConstants.ROLE_DELETE)
    @PostMapping("/batch-delete")
    public Response<Void> batchDeleteRole(@RequestBody @Valid RoleBatchDeleteDto dto) {
        roleService.batchDeleteRole(dto);
        return Response.success();
    }

    @Operation(summary = "切换角色状态（启用/禁用）")
    @RequiresPermission(PermissionConstants.ROLE_UPDATE)
    @PostMapping("/switch-status")
    public Response<Void> switchRoleStatus(@RequestBody @Valid RoleStatusSwitchDto dto) {
        roleService.switchRoleStatus(dto);
        return Response.success();
    }

    @Operation(summary = "为角色分配权限（覆盖）")
    @RequiresPermission(PermissionConstants.ROLE_ASSIGN)
    @PostMapping("/assign-permissions")
    public Response<Void> assignPermissions(@RequestBody @Valid RoleAssignPermissionDto dto) {
        roleService.assignPermissions(dto);
        return Response.success();
    }

    @Operation(summary = "查询角色已分配的权限ID列表")
    @RequiresPermission(PermissionConstants.ROLE_ASSIGN)
    @GetMapping("/{roleId}/permission-ids")
    public Response<List<Long>> listPermissionIds(@PathVariable Long roleId) {
        return Response.success(roleService.listPermissionIdsByRoleId(roleId));
    }
}