package com.mms.gateway.utils;

import com.mms.common.core.constants.gateway.GatewayConstants;
import com.mms.common.core.utils.IdUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

/**
 * 实现功能【网关 TraceId 工具类】
 * <p>
 * 统一处理 TraceId 的生成和提取逻辑
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-01 15:54:19
 */
public class GatewayTraceUtils {

    /**
     * 从请求中提取或生成 TraceId
     * <p>
     * 如果请求头中已存在 TraceId，则使用现有的；否则生成新的
     * </p>
     *
     * @param exchange 请求交换对象
     * @return TraceId
     */
    public static String getOrGenerateTraceId(ServerWebExchange exchange) {
        String incomingTraceId = exchange.getRequest().getHeaders().getFirst(GatewayConstants.Headers.TRACE_ID);
        return StringUtils.hasText(incomingTraceId) ? incomingTraceId : generateTraceId();
    }

    /**
     * 生成新的 TraceId
     * <p>
     * 使用 UUID 生成，并移除连字符以保持简洁
     * </p>
     *
     * @return 生成的 TraceId
     */
    public static String generateTraceId() {
        return IdUtils.timestampId();
    }

    /**
     * 从请求头中提取 TraceId
     *
     * @param exchange 请求交换对象
     * @return TraceId，如果不存在则返回 null
     */
    public static String getTraceId(ServerWebExchange exchange) {
        return exchange.getRequest().getHeaders().getFirst(GatewayConstants.Headers.TRACE_ID);
    }

    /**
     * 私有构造函数，防止实例化
     */
    private GatewayTraceUtils() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }
}

