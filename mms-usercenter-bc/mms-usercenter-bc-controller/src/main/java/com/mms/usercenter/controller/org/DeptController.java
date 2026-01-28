package com.mms.usercenter.controller.org;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.core.constants.usercenter.PermissionConstants;
import com.mms.common.core.response.Response;
import com.mms.common.security.annotations.RequiresPermission;
import com.mms.usercenter.common.auth.dto.UserAssignDeptDto;
import com.mms.usercenter.common.org.dto.*;
import com.mms.usercenter.common.org.vo.DeptVo;
import com.mms.usercenter.service.org.service.DeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 实现功能【部门管理 Controller】
 * <p>
 * 提供部门管理的REST API接口
 * </p>
 *
 * @author li.hongyu
 * @date 2026-01-23 10:34:12
 */
@Tag(name = "部门管理", description = "部门管理相关接口")
@RestController
@RequestMapping("/dept")
public class DeptController {

    @Resource
    private DeptService deptService;

    @Operation(summary = "分页查询部门列表", description = "根据条件分页查询部门列表")
    @RequiresPermission(PermissionConstants.DEPT_VIEW)
    @PostMapping("/page")
    public Response<Page<DeptVo>> getDeptPage(@RequestBody @Valid DeptPageQueryDto dto) {
        return Response.success(deptService.getDeptPage(dto));
    }

    @Operation(summary = "查询部门树（全量）", description = "根据条件查询全量部门树")
    @RequiresPermission(PermissionConstants.DEPT_VIEW)
    @PostMapping("/tree")
    public Response<java.util.List<DeptVo>> listDeptTree(@RequestBody @Valid DeptTreeQueryDto dto) {
        return Response.success(deptService.listDeptTree(dto));
    }

    @Operation(summary = "根据ID查询部门详情", description = "根据部门ID查询部门详细信息")
    @RequiresPermission(PermissionConstants.DEPT_VIEW)
    @GetMapping("/{deptId}")
    public Response<DeptVo> getDeptById(@PathVariable Long deptId) {
        return Response.success(deptService.getDeptById(deptId));
    }

    @Operation(summary = "创建部门", description = "创建新部门")
    @RequiresPermission(PermissionConstants.DEPT_CREATE)
    @PostMapping("/create")
    public Response<DeptVo> createDept(@RequestBody @Valid DeptCreateDto dto) {
        return Response.success(deptService.createDept(dto));
    }

    @Operation(summary = "更新部门信息", description = "更新部门的基本信息")
    @RequiresPermission(PermissionConstants.DEPT_UPDATE)
    @PutMapping("/update")
    public Response<DeptVo> updateDept(@RequestBody @Valid DeptUpdateDto dto) {
        return Response.success(deptService.updateDept(dto));
    }

    @Operation(summary = "删除部门", description = "逻辑删除部门（软删除）")
    @RequiresPermission(PermissionConstants.DEPT_DELETE)
    @DeleteMapping("/{deptId}")
    public Response<Void> deleteDept(@PathVariable Long deptId) {
        deptService.deleteDept(deptId);
        return Response.success();
    }

    @Operation(summary = "批量删除部门", description = "批量逻辑删除部门（软删除）")
    @RequiresPermission(PermissionConstants.DEPT_DELETE)
    @PostMapping("/batch-delete")
    public Response<Void> batchDeleteDept(@RequestBody @Valid DeptBatchDeleteDto dto) {
        deptService.batchDeleteDept(dto);
        return Response.success();
    }

    @Operation(summary = "切换部门状态", description = "启用或禁用部门")
    @RequiresPermission(PermissionConstants.DEPT_UPDATE)
    @PostMapping("/switch-status")
    public Response<Void> switchDeptStatus(@RequestBody @Valid DeptStatusSwitchDto dto) {
        deptService.switchDeptStatus(dto);
        return Response.success();
    }

    @Operation(summary = "为用户分配部门（覆盖）", description = "为用户分配部门，会覆盖原有部门关联")
    @RequiresPermission(PermissionConstants.DEPT_UPDATE)
    @PostMapping("/assign-depts")
    public Response<Void> assignDepts(@RequestBody @Valid UserAssignDeptDto dto) {
        deptService.assignDepts(dto);
        return Response.success();
    }

    @Operation(summary = "查询用户已分配的部门ID列表", description = "查询用户当前所属的部门ID列表")
    @RequiresPermission(PermissionConstants.DEPT_VIEW)
    @GetMapping("/{userId}/dept-ids")
    public Response<java.util.List<Long>> listDeptIds(@PathVariable Long userId) {
        return Response.success(deptService.listDeptIdsByUserId(userId));
    }
}