package com.mms.usercenter.service.auth.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.usercenter.common.auth.dto.RoleAssignPermissionDto;
import com.mms.usercenter.common.auth.dto.RoleBatchDeleteDto;
import com.mms.usercenter.common.auth.dto.RoleCreateDto;
import com.mms.usercenter.common.auth.dto.RolePageQueryDto;
import com.mms.usercenter.common.auth.dto.RoleRemoveUserDto;
import com.mms.usercenter.common.auth.dto.RoleStatusSwitchDto;
import com.mms.usercenter.common.auth.dto.RoleUpdateDto;
import com.mms.usercenter.common.auth.vo.RoleVo;
import com.mms.usercenter.common.auth.vo.UserDetailVo;

import java.util.List;

/**
 * 实现功能【角色服务】
 * <p>
 * 提供角色管理的核心业务方法
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-12 09:23:23
 */
public interface RoleService {

    /**
     * 分页查询角色列表
     */
    Page<RoleVo> getRolePage(RolePageQueryDto dto);

    /**
     * 根据ID查询角色详情
     */
    RoleVo getRoleById(Long roleId);

    /**
     * 创建角色
     */
    RoleVo createRole(RoleCreateDto dto);

    /**
     * 更新角色
     */
    RoleVo updateRole(RoleUpdateDto dto);

    /**
     * 删除角色（逻辑删除）
     */
    void deleteRole(Long roleId);

    /**
     * 批量删除角色（逻辑删除）
     */
    void batchDeleteRole(RoleBatchDeleteDto dto);

    /**
     * 切换角色状态（启用/禁用）
     */
    void switchRoleStatus(RoleStatusSwitchDto dto);

    /**
     * 为角色分配权限（覆盖）
     */
    void assignPermissions(RoleAssignPermissionDto dto);

    /**
     * 查询角色当前拥有的权限ID列表
     */
    List<Long> listPermissionIdsByRoleId(Long roleId);

    /**
     * 查询角色关联的用户列表
     */
    List<UserDetailVo> listUsersByRoleId(Long roleId);

    /**
     * 移除角色的用户关联
     */
    void removeUserFromRole(RoleRemoveUserDto dto);

}