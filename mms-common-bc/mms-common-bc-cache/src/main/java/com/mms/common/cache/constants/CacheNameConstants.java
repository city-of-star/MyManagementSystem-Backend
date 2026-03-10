package com.mms.common.cache.constants;

/**
 * 实现功能【缓存名称常量类】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-03-10 13:47:24
 */
public final class CacheNameConstants {

    /**
     * 用户中心服务相关缓存
     */
    public static final class UserCenter {

        /**
         * 用户认证信息
         */
        public static final String USER_AUTH_INFO = CacheKeyPrefixConstants.USERCENTER + "userAuthInfo:";

        /**
         * 用户权限
         */
        public static final String USER_AUTHORITY = CacheKeyPrefixConstants.USERCENTER + "userAuthority:";
    }

    /**
     * 基础数据服务相关缓存
     */
    public static final class Base {

        /**
         * 字典数据
         */
        public static final String DICT_DATE = CacheKeyPrefixConstants.BASE + "dictDate:";
    }

}