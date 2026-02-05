package com.mms.usercenter.service.auth.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.usercenter.common.auth.dto.*;
import com.mms.usercenter.common.auth.vo.UserDetailVo;
import com.mms.usercenter.common.auth.vo.UserPageVo;

import java.util.List;

/**
 * 实现功能【用户服务】
 * <p>
 * 提供用户管理的核心业务方法
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-19 09:50:52
 */
public interface UserService {

    /**
     * 分页查询用户列表
     *
     * @param dto 查询条件
     * @return 分页用户列表
     */
    Page<UserPageVo> getUserPage(UserPageQueryDto dto);

    /**
     * 根据用户ID查询用户详情
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    UserDetailVo getUserById(Long userId);

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    UserDetailVo getUserByUsername(String username);

    /**
     * 创建用户
     *
     * @param dto 用户创建参数
     * @return 创建的用户信息
     */
    UserDetailVo createUser(UserCreateDto dto);

    /**
     * 更新用户信息
     *
     * @param dto 用户更新参数
     * @return 更新后的用户信息
     */
    UserDetailVo updateUser(UserUpdateDto dto);

    /**
     * 删除用户（逻辑删除）
     *
     * @param userId 用户ID
     */
    void deleteUser(Long userId);

    /**
     * 批量删除用户（逻辑删除）
     *
     * @param dto 批量删除参数
     */
    void batchDeleteUser(UserBatchDeleteDto dto);

    /**
     * 切换用户状态（启用/禁用）
     *
     * @param dto 状态切换参数
     */
    void switchUserStatus(UserStatusSwitchDto dto);

    /**
     * 锁定/解锁用户
     *
     * @param dto 锁定参数
     */
    void lockOrUnlockUser(UserLockDto dto);

    /**
     * 重置用户密码（管理员操作）
     *
     * @param dto 密码重置参数
     */
    void resetPassword(UserPasswordResetDto dto);

    /**
     * 修改用户密码（用户自己操作）
     *
     * @param dto 密码修改参数
     */
    void changePassword(UserPasswordChangeDto dto);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return true-存在，false-不存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @return true-存在，false-不存在
     */
    boolean existsByEmail(String email);

    /**
     * 检查手机号是否存在
     *
     * @param phone 手机号
     * @return true-存在，false-不存在
     */
    boolean existsByPhone(String phone);

    /**
     * 为用户分配角色（覆盖）
     *
     * @param dto 用户分配角色参数
     */
    void assignRoles(UserAssignRoleDto dto);

    /**
     * 查询用户当前拥有的角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> listRoleIdsByUserId(Long userId);
}