package com.mms.usercenter.server.security;

import com.mms.common.security.servlet.filter.UserAuthorityProvider;
import com.mms.common.security.core.vo.UserAuthorityVo;
import com.mms.usercenter.service.security.service.UserAuthorityService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 实现功能【用户角色权限提供实现类】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-03-13 15:58:58
 */
@AllArgsConstructor
@Component
public class UserAuthorityProviderImpl implements UserAuthorityProvider {

    private final UserAuthorityService userAuthorityService;

    @Override
    public UserAuthorityVo getUserAuthoritiesFromSource(String username) {
        return userAuthorityService.getUserAuthorities(username);
    }
}