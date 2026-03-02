package com.mms.common.cache.constants;

import java.util.concurrent.TimeUnit;

/**
 * 实现功能【缓存 TTL 约定常量类】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-03-02 11:33:37
 */
public final class CacheTtl {

    /**
     * 短期缓存：5 分钟（适合验证码、临时状态）
     */
    public static final long SHORT_SECONDS = 5 * 60;

    /**
     * 中期缓存：30 分钟（适合列表页、查询结果等）
     */
    public static final long MEDIUM_SECONDS = 30 * 60;

    /**
     * 长期缓存：1 小时（适合用户详情、权限等读多写少的数据）
     */
    public static final long LONG_SECONDS = 60 * 60;

    /**
     * 超长期缓存：1 天（适合字典、配置等很少变化的数据）
     */
    public static final long VERY_LONG_SECONDS = 24 * 60 * 60;

    /**
     * 默认时间单位：秒
     */
    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;

    /**
     * 私有构造函数，防止实例化
     */
    private CacheTtl() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}

