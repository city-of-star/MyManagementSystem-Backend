package com.mms.usercenter.service.auth.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.usercenter.common.auth.dto.PermissionBatchDeleteDto;
import com.mms.usercenter.common.auth.dto.PermissionCreateDto;
import com.mms.usercenter.common.auth.dto.PermissionPageQueryDto;
import com.mms.usercenter.common.auth.dto.PermissionRemoveRoleDto;
import com.mms.usercenter.common.auth.dto.PermissionStatusSwitchDto;
import com.mms.usercenter.common.auth.dto.PermissionTreeQueryDto;
import com.mms.usercenter.common.auth.dto.PermissionUpdateDto;
import com.mms.usercenter.common.auth.vo.PermissionVo;
import com.mms.usercenter.common.auth.vo.RoleVo;

import java.util.List;

/**
 * 实现功能【权限服务】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-12 09:24:06
 */
public interface PermissionService {

    /**
     * 分页查询权限列表
     */
    Page<PermissionVo> getPermissionPage(PermissionPageQueryDto dto);

    /**
     * 查询权限详情
     */
    PermissionVo getPermissionById(Long permissionId);

    /**
     * 创建权限
     */
    PermissionVo createPermission(PermissionCreateDto dto);

    /**
     * 更新权限
     */
    PermissionVo updatePermission(PermissionUpdateDto dto);

    /**
     * 删除权限（逻辑删除）
     */
    void deletePermission(Long permissionId);

    /**
     * 批量删除权限（逻辑删除）
     */
    void batchDeletePermission(PermissionBatchDeleteDto dto);

    /**
     * 切换权限状态（启用/禁用）
     */
    void switchPermissionStatus(PermissionStatusSwitchDto dto);

    /**
     * 返回权限树，可按名称/编码/类型/状态/可见性过滤
     */
    List<PermissionVo> listPermissionTree(PermissionTreeQueryDto dto);

    /**
     * 返回当前用户有权限的权限树（用于前端菜单展示）
     * 固定查询条件：只返回启用(status=1)、可见(visible=1)、类型为目录(catalog)或菜单(menu)的权限
     */
    List<PermissionVo> listCurrentUserPermissionTree();

    /**
     * 查询与指定权限关联的角色列表
     */
    List<RoleVo> listRolesByPermissionId(Long permissionId);

    /**
     * 移除权限与角色的关联关系
     */
    void removeRoleFromPermission(PermissionRemoveRoleDto dto);
}