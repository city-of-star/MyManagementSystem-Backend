package com.mms.common.security.constants;

/**
 * 实现功能【JWT常量类】
 * <p>
 * 统一管理JWT相关的常量定义
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-05 09:42:26
 */
public final class JwtConstants {

    /**
     * 请求头常量
     */
    public static final class Headers {
        /**
         * Authorization 请求头
         */
        public static final String AUTHORIZATION = "Authorization";

        /**
         * Bearer Token 前缀
         */
        public static final String BEARER_PREFIX = "Bearer ";

        /**
         * 私有构造函数，防止实例化
         */
        private Headers() {
            throw new UnsupportedOperationException("常量类不允许实例化");
        }
    }

    /**
     * JWT Claims键名常量
     */
    public static final class Claims {
        /**
         * JWT Claims中的用户名键
         */
        public static final String USERNAME = "username";

        /**
         * JWT Claims中的用户ID键
         */
        public static final String USER_ID = "userId";

        /**
         * JWT Claims中的Token类型键
         */
        public static final String TOKEN_TYPE = "tokenType";

        /**
         * 私有构造函数，防止实例化
         */
        private Claims() {
            throw new UnsupportedOperationException("常量类不允许实例化");
        }
    }

    /**
     * 缓存（Redis）Key前缀常量
     */
    public static final class CacheKeys {
        /**
         * Redis中Token黑名单的key前缀
         */
        public static final String TOKEN_BLACKLIST_PREFIX = "mms:auth:blacklist:";

        /**
         * Redis中Refresh Token存储的key前缀
         */
        public static final String REFRESH_TOKEN_PREFIX = "mms:auth:refresh:";

        /**
         * 私有构造函数，防止实例化
         */
        private CacheKeys() {
            throw new UnsupportedOperationException("常量类不允许实例化");
        }
    }

    /**
     * 私有构造函数，防止实例化
     */
    private JwtConstants() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}