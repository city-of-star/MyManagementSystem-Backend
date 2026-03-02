package com.mms.usercenter.common.security.constants;

import com.mms.common.cache.constants.CacheKeyPrefix;

/**
 * 实现功能【Redis缓存前缀-用户认证常量】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-19 16:55:20
 */
public final class UserAuthorityConstants {

    /**
     * 认证相关缓存前缀
     */
    public static final String USERCENTER_AUTH = CacheKeyPrefix.USERCENTER + "auth:";

    /**
     * 用户角色集合缓存前缀
     */
    public static final String USER_ROLE_PREFIX = USERCENTER_AUTH + "roles:";

    /**
     * 用户权限集合缓存前缀
     */
    public static final String USER_PERMISSION_PREFIX = USERCENTER_AUTH + "perms:";

    /**
     * 私有构造函数，防止实例化
     */
    private UserAuthorityConstants() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}
