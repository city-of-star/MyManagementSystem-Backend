package com.mms.common.security.core.constants;

import com.mms.common.cache.constants.CacheKeyPrefixConstants;

/**
 * 实现功能【JWT缓存前缀】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-05 09:42:26
 */
public final class JwtCacheKeyConstants {

    /**
     * JWT相关缓存前缀
     */
    public static final String SECURITY_JWT = CacheKeyPrefixConstants.SECURITY + "jwt:";

    /**
     * Token黑名单的key前缀
     */
    public static final String TOKEN_BLACKLIST_PREFIX = SECURITY_JWT + "blacklist:";

    /**
     * Refresh Token存储的key前缀
     */
    public static final String REFRESH_TOKEN_PREFIX = SECURITY_JWT + "refresh:";

    /**
     * 私有构造函数，防止实例化
     */
    private JwtCacheKeyConstants() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}