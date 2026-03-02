package com.mms.common.cache.constants;

/**
 * 实现功能【缓存Key前缀常量类】
 * <p>
 * 统一管理各业务模块的缓存Key前缀，避免硬编码
 * 命名规范：业务模块:功能:标识
 * </p>
 *
 * @author li.hongyu
 * @date 2026-03-02 09:48:24
 */
public final class CacheKeyPrefix {

    /**
     * 用户相关缓存前缀
     */
    public static final String USER = "mms:cache:user:";

    /**
     * 权限相关缓存前缀
     */
    public static final String PERMISSION = "mms:cache:permission:";

    /**
     * 角色相关缓存前缀
     */
    public static final String ROLE = "mms:cache:role:";

    /**
     * 认证相关缓存前缀
     */
    public static final String AUTH = "mms:cache:auth:";

    /**
     * Token黑名单前缀
     */
    public static final String TOKEN_BLACKLIST = "mms:cache:token:blacklist:";

    /**
     * Refresh Token前缀
     */
    public static final String REFRESH_TOKEN = "mms:cache:token:refresh:";

    /**
     * 验证码前缀
     */
    public static final String CAPTCHA = "mms:cache:captcha:";

    /**
     * 分布式锁前缀
     */
    public static final String LOCK = "mms:cache:lock:";

    /**
     * 私有构造函数，防止实例化
     */
    private CacheKeyPrefix() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}
