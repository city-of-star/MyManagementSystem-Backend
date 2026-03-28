package com.mms.intern.server.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mms.common.cache.constants.CacheNameConstants;
import com.mms.common.cache.utils.RedisUtils;
import com.mms.common.security.core.vo.UserAuthorityVo;
import com.mms.common.security.servlet.filter.UserAuthorityProvider;
import com.mms.usercenter.feign.UserAuthorityFeign;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserAuthorityProviderImpl implements UserAuthorityProvider {

    private final UserAuthorityFeign userAuthorityFeign;

    @Override
    public UserAuthorityVo getUserAuthoritiesFromSource(String username) {
        UserAuthorityVo cached = RedisUtils.get(CacheNameConstants.UserCenter.USER_AUTHORITY + username,
                new TypeReference<UserAuthorityVo>() {});
        if (cached != null) {
            return cached;
        }
        return userAuthorityFeign.getUserAuthorities(username).getData();
    }
}
