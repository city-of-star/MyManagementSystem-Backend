package com.mms.common.web.filter;

import com.mms.common.core.constants.gateway.GatewayConstants;
import com.mms.common.core.utils.IdUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import java.io.IOException;

/**
 * 实现功能【Servlet/MVC TraceId 过滤器】
 * <p>
 * - 从请求头读取 X-Trace-Id（若无则生成）
 * - 放入 MDC("traceId")，以便 Response 序列化时写回 traceId
 * </p>
 *
 * @author li.hongyu
 * @date 2025-11-10 15:36:17
 */
@Component
public class TraceIdMvcFilter implements Filter, Ordered {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request; // 只关心 HTTP 请求

		// 优先从网关读取追踪 ID
		String traceId = httpRequest.getHeader(GatewayConstants.Headers.TRACE_ID);
		// 若网关未提供，则本地生成
		if (!StringUtils.hasText(traceId)) {
			traceId = generateTraceId();
		}

		// 将追踪 ID 放进 MDC，便于日志打印和响应序列化写回
		MDC.put(GatewayConstants.Mdc.TRACE_ID, traceId);

		try {
			// 放行给下游（Controller/拦截器等）
			chain.doFilter(request, response);
		} finally {
			// 清理 MDC，防止线程复用造成脏数据
			MDC.remove(GatewayConstants.Mdc.TRACE_ID);
		}
	}

	/**
	 * 生成追踪 ID
	 */
	private String generateTraceId() {
		// 使用统一的全局ID生成工具类生成：时间戳 + 短UUID
		return IdUtils.timestampId();
	}

	@Override
	public int getOrder() {
		// 最先执行
		return GatewayConstants.FilterOrder.TRACE_FILTER;
	}
}

