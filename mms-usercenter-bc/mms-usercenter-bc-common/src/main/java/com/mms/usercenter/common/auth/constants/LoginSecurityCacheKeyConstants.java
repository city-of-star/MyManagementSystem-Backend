package com.mms.usercenter.common.auth.constants;

import com.mms.common.cache.constants.CacheKeyPrefixConstants;

/**
 * 实现功能【Redis缓存前缀-登录安全常量】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-29 15:54:54
 */
public final class LoginSecurityCacheKeyConstants {

    /**
     * 登录相关缓存前缀
     */
    public static final String USERCENTER_LOGIN = CacheKeyPrefixConstants.USERCENTER + "login:";

    /**
     * 登录失败次数缓存前缀
     */
    public static final String LOGIN_ATTEMPT_PREFIX = USERCENTER_LOGIN + "attempts:";

    /**
     * 账号锁定状态缓存前缀
     */
    public static final String ACCOUNT_LOCK_PREFIX = USERCENTER_LOGIN + "lock:";

    /**
     * 私有构造函数，防止实例化
     */
    private LoginSecurityCacheKeyConstants() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }

}