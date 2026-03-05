package com.mms.usercenter.service.security.service;

import com.mms.usercenter.common.security.entity.SecurityUser;
import com.mms.usercenter.common.security.vo.UserAuthorityVo;

/**
 * 实现功能【用户认证服务】
 * <p>
 * - 用户认证信息查询（带缓存）
 * - 用户角色/权限查询（（带缓存）
 * - 删除用户认证信息缓存
 * - 删除用户角色/权限缓存
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-23 20:31:11
 */
public interface UserAuthorityService {

    /**
     * 根据用户名查询用户认证信息（带缓存）
     * @param username 用户名
     * @return 用户认证信息
     */
    SecurityUser getSecurityUserByUsername(String username);

    /**
     * 根据用户名查询角色与权限（带缓存）
     * @param username 用户名
     * @return 用户角色/权限返回对象
     */
    UserAuthorityVo getUserAuthorities(String username);

    /**
     * 清除指定用户的认证信息缓存
     * @param username 用户名
     */
    void clearSecurityUserByUsername(String username);

    /**
     * 清除指定用户的权限缓存
     * @param userId 用户ID
     */
    void clearUserAuthorityCacheByUserId(Long userId);

    /**
     * 清除拥有指定角色的所有用户的权限缓存
     * @param roleId 角色ID
     */
    void clearUserAuthorityCacheByRoleId(Long roleId);

    /**
     * 清除包含指定权限的所有角色下用户的权限缓存
     * @param permissionId 权限ID
     */
    void clearUserAuthorityCacheByPermissionId(Long permissionId);
}

