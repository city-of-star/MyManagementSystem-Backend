package com.mms.common.webmvc.audit;

/**
 * 实现功能【操作日志采集常量】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-05-29 14:00:00
 */
public final class OperationLogConstants {

    /**
     * 请求体缓存上限（字节）
     */
    public static final int REQUEST_BODY_CACHE_LIMIT = 32 * 1024;

    /**
     * 请求参数序列化上限（字符）
     */
    public static final int REQUEST_PARAMS_MAX_LENGTH = 32 * 1024;

    private OperationLogConstants() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}
