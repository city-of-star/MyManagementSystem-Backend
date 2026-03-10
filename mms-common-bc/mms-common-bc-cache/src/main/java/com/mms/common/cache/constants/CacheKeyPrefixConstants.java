package com.mms.common.cache.constants;

/**
 * 实现功能【缓存Key前缀常量类】
 * <p>
 * 统一管理各业务模块的缓存Key前缀，避免硬编码
 * </p>
 *
 * @author li.hongyu
 * @date 2026-03-02 09:48:24
 */
public final class CacheKeyPrefixConstants {

    /**
     * 通用安全模块缓存前缀
     */
    public static final String SECURITY = "mms:cache:security:";

    /**
     * 网关服务缓存前缀
     */
    public static final String GATEWAY = "mms:cache:gateway:";

    /**
     * 用户中心服务缓存前缀
     */
    public static final String USERCENTER = "mms:cache:usercenter:";

    /**
     * 基础数据服务缓存前缀
     */
    public static final String BASE = "mms:cache:base:";

    /**
     * 定时任务服务缓存前缀
     */
     public static final String JOB = "mms:cache:job:";

    /**
     * 私有构造函数，防止实例化
     */
    private CacheKeyPrefixConstants() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}
