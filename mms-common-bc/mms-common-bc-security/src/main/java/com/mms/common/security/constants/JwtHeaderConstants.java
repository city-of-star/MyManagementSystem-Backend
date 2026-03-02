package com.mms.common.security.constants;

/**
 * 实现功能【】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-05 09:42:26
 */
public final class JwtHeaderConstants {

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
    private JwtHeaderConstants() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}