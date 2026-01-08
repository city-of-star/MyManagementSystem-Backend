package com.mms.usercenter.service.auth.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.usercenter.common.auth.dto.PermissionBatchDeleteDto;
import com.mms.usercenter.common.auth.dto.PermissionCreateDto;
import com.mms.usercenter.common.auth.dto.PermissionPageQueryDto;
import com.mms.usercenter.common.auth.dto.PermissionStatusSwitchDto;
import com.mms.usercenter.common.auth.dto.PermissionUpdateDto;
import com.mms.usercenter.common.auth.vo.PermissionVo;

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
     * 返回权限树，可按类型/状态/可见性过滤
     *
     * @param permissionType 可选：menu/button/api
     * @param status         可选：0/1
     * @param visible        可选：0/1
     */
    List<PermissionVo> listPermissionTree(String permissionType, Integer status, Integer visible);

    /**
     * 返回当前用户有权限的权限树（用于前端菜单展示）
     * 只返回用户拥有权限编码的权限节点，并按类型/状态/可见性过滤
     *
     * @param permissionType 可选：menu/button/api
     * @param status         可选：0/1
     * @param visible        可选：0/1
     */
    List<PermissionVo> listCurrentUserPermissionTree(String permissionType, Integer status, Integer visible);
}