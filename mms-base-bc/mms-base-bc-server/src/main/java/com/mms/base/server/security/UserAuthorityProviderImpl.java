package com.mms.base.server.security;

import com.mms.common.security.filter.UserAuthorityProvider;
import com.mms.common.security.vo.UserAuthorityVo;
import com.mms.usercenter.feign.UserAuthorityFeign;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 实现功能【用户角色权限提供实现类】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-03-13 16:01:13
 */
@AllArgsConstructor
@Component
public class UserAuthorityProviderImpl implements UserAuthorityProvider {

    private final UserAuthorityFeign userAuthorityFeign;

    @Override
    public UserAuthorityVo getUserAuthoritiesFromSource(String username) {
        return userAuthorityFeign.getUserAuthorities(username).getData();
    }
}