package com.mms.gateway.filter;

import com.mms.common.core.constants.gateway.GatewayConstants;
import com.mms.gateway.utils.GatewayMdcUtils;
import com.mms.gateway.utils.GatewayTraceUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
/**
 * 实现功能【TraceId 追踪过滤器】
 * <p>
 * - 每个请求生成/提取 traceId
 * - 放入请求头并写入 MDC
 * <p>
 *
 * @author li.hongyu
 * @date 2025-11-10 15:36:17
 */
@Component
public class TraceFilter implements GlobalFilter, Ordered {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		// 从请求头读取 traceId；若不存在则生成新的 traceId
		String traceId = GatewayTraceUtils.getOrGenerateTraceId(exchange);

		// 写入 MDC，便于日志打印关联
		GatewayMdcUtils.putTraceId(traceId);

		// 将 traceId 透传到下游服务（写入请求头）
		ServerHttpRequest mutated = exchange.getRequest()
				.mutate()
				.header(GatewayConstants.Headers.TRACE_ID, traceId) // 添加追踪 ID 到请求头
				.build();

		// 继续过滤器链，并在完成后清理 MDC
		return chain.filter(exchange.mutate().request(mutated).build())
				.doFinally(signalType -> GatewayMdcUtils.removeTraceId()); // 清理 MDC，避免线程复用污染
	}

	@Override
	public int getOrder() {
		// 在 GatewayExceptionAdvice 之后执行
		return GatewayConstants.FilterOrder.TRACE_FILTER;
	}
}


