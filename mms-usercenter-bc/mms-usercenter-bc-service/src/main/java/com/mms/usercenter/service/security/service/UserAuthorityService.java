package com.mms.usercenter.service.security.service;

import com.mms.usercenter.common.security.vo.UserAuthorityVo;

/**
 * 实现功能【用户权限服务】
 * <p>
 * - 用户角色/权限查询服务
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-23 20:31:11
 */
public interface UserAuthorityService {

    /**
     * 根据用户名查询角色与权限（带缓存）
     */
    UserAuthorityVo getUserAuthorities(String username);

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
}

