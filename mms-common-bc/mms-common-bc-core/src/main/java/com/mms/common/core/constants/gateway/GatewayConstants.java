package com.mms.common.core.constants.gateway;

/**
 * 实现功能【网关常量类】
 * <p>
 * 统一管理网关服务中使用的所有常量，避免重复定义
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-01 14:46:18
 */
public final class GatewayConstants {

    /**
     * 请求头常量（网关透传/链路相关）
     */
    public static final class Headers {
        /**
         * TraceId 请求头
         */
        public static final String TRACE_ID = "X-Trace-Id";

        /**
         * 用户名请求头（透传到下游服务）
         */
        public static final String USER_NAME = "X-User-Name";

        /**
         * 用户ID请求头（透传到下游服务）
         */
        public static final String USER_ID = "X-User-Id";

        /**
         * Token Jti请求头（透传到下游服务）
         */
        public static final String TOKEN_JTI = "X-Token-Jti";

        /**
         * Token过期时间请求头（透传到下游服务，时间戳毫秒数）
         */
        public static final String TOKEN_EXP = "X-Token-Exp";

        /**
         * 客户端IP请求头（透传到下游服务）
         */
        public static final String CLIENT_IP = "X-Client-Ip";

        /**
         * 用户代理请求头（透传到下游服务）
         */
        public static final String USER_AGENT = "X-User-Agent";

        /**
         * 登录地点请求头（透传到下游服务）
         */
        public static final String LOGIN_LOCATION = "X-Login-Location";

        /**
         * 网关签名请求头（透传到下游服务，用于验证请求来源）
         */
        public static final String GATEWAY_SIGNATURE = "X-Gateway-Signature";

        /**
         * 网关签名时间戳请求头（透传到下游服务，用于防重放攻击）
         */
        public static final String GATEWAY_TIMESTAMP = "X-Gateway-Timestamp";

        /**
         * 私有构造函数，防止实例化
         */
        private Headers() {
            throw new UnsupportedOperationException("常量类不允许实例化");
        }
    }

    /**
     * MDC 常量
     */
    public static final class Mdc {
        /**
         * TraceId 在 MDC 中的键名
         */
        public static final String TRACE_ID = "traceId";

        /**
         * 私有构造函数，防止实例化
         */
        private Mdc() {
            throw new UnsupportedOperationException("常量类不允许实例化");
        }
    }

    /**
     * 过滤器顺序常量
     */
    public static final class FilterOrder {
        /**
         * 全局异常处理器的执行顺序（最高优先级，确保最先处理异常）
         */
        public static final int GLOBAL_EXCEPTION_HANDLER = Integer.MIN_VALUE + 100;

        /**
         * TraceFilter 的执行顺序（在异常处理器之后）
         */
        public static final int TRACE_FILTER = GLOBAL_EXCEPTION_HANDLER + 100;

        /**
         * ClientIpFilter 的执行顺序（在 TraceFilter 之后）
         */
        public static final int CLIENT_IP_FILTER = TRACE_FILTER + 100;

        /**
         * JwtAuthFilter 的执行顺序（在 ClientIpFilter 之后）
         */
        public static final int JWT_AUTH_FILTER = CLIENT_IP_FILTER + 100;

        /**
         * 私有构造函数，防止实例化
         */
        private FilterOrder() {
            throw new UnsupportedOperationException("常量类不允许实例化");
        }
    }

    /**
     * 私有构造函数，防止实例化
     */
    private GatewayConstants() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}

