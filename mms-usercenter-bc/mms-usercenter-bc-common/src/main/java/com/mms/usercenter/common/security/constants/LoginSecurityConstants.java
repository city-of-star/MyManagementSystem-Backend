package com.mms.usercenter.common.security.constants;

/**
 * 实现功能【登录安全常量】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-29 15:54:54
 */
public final class LoginSecurityConstants {

    /**
     * 登录失败次数缓存前缀
     * 示例：mms:usercenter:login:attempts:{username}
     */
    public static final String LOGIN_ATTEMPT_PREFIX = "mms:usercenter:login:attempts:";

    /**
     * 账号锁定状态缓存前缀
     * 示例：mms:usercenter:login:lock:{username}
     */
    public static final String ACCOUNT_LOCK_PREFIX = "mms:usercenter:login:lock:";

    /**
     * 私有构造函数，防止实例化
     */
    private LoginSecurityConstants() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }

}