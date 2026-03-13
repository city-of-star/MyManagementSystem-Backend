package com.mms.common.security.filter;

import com.mms.common.security.vo.UserAuthorityVo;

/**
 * 实现功能【用户角色权限提供接口】
 * <p>
 * 需各服务实现此接口来提供权限信息，供通用的Jwt过滤器使用
 * <p>
 *
 * @author li.hongyu
 * @date 2026-03-13 15:52:45
 */
public interface UserAuthorityProvider {

    /**
     * 根据用户名获取用户角色和权限
     *
     * @param username 用户名
     * @return 用户角色和权限信息
     */
    UserAuthorityVo getUserAuthoritiesFromSource(String username);
}