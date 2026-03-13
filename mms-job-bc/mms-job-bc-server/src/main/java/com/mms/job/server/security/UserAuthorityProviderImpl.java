package com.mms.job.server.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mms.common.cache.constants.CacheNameConstants;
import com.mms.common.cache.utils.RedisUtils;
import com.mms.common.security.servlet.filter.UserAuthorityProvider;
import com.mms.common.security.core.vo.UserAuthorityVo;
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
 * @date 2026-03-13 16:02:31
 */
@AllArgsConstructor
@Component
public class UserAuthorityProviderImpl implements UserAuthorityProvider {

    private final UserAuthorityFeign userAuthorityFeign;

    @Override
    public UserAuthorityVo getUserAuthoritiesFromSource(String username) {
        // 先从缓存查
        UserAuthorityVo userAuthorityVo = RedisUtils.get(CacheNameConstants.UserCenter.USER_AUTHORITY  + username, new TypeReference<UserAuthorityVo>() {});
        if (userAuthorityVo == null) {
            // 缓存没命中，从用户中心查
            userAuthorityVo = userAuthorityFeign.getUserAuthorities(username).getData();
        }
        return userAuthorityVo;
    }
}