package com.mms.common.core.constants.usercenter;

/**
 * 实现功能【用户中心 Redis 缓存相关常量】
 * <p>
 * 统一管理 Key 前缀、TTL 等缓存约定
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-19 16:55:20
 */
public final class UserAuthorityConstants {

    /**
     * 用户角色集合缓存前缀
     * 示例：mms:usercenter:roles:{username}
     */
    public static final String USER_ROLE_PREFIX = "mms:usercenter:roles:";

    /**
     * 用户权限集合缓存前缀
     * 示例：mms:usercenter:perms:{username}
     */
    public static final String USER_PERMISSION_PREFIX = "mms:usercenter:perms:";

    /**
     * Spring Security 角色前缀
     */
    public static final String ROLE_PREFIX = "ROLE_";

    /**
     * 角色、权限缓存默认过期时间（分钟）
     */
    public static final long ROLE_PERMISSION_CACHE_TTL_MINUTES = 30L;

    /**
     * 私有构造函数，防止实例化
     */
    private UserAuthorityConstants() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}


