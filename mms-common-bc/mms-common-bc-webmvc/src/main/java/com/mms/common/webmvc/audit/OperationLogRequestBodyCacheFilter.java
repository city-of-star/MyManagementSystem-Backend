package com.mms.common.webmvc.audit;

import com.mms.common.core.constants.gateway.GatewayConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

/**
 * 实现功能【JSON 请求体缓存过滤器】
 * <p>
 * 为操作日志采集提供可重复读取的请求体；仅处理 application/json 请求。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-29 14:00:00
 */
public class OperationLogRequestBodyCacheFilter extends OncePerRequestFilter implements Ordered {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!shouldWrap(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(
                request, OperationLogConstants.REQUEST_BODY_CACHE_LIMIT);
        filterChain.doFilter(wrappedRequest, response);
    }

    private boolean shouldWrap(HttpServletRequest request) {
        String contentType = request.getContentType();
        if (contentType == null) {
            return false;
        }
        return contentType.toLowerCase().startsWith(MediaType.APPLICATION_JSON_VALUE);
    }

    @Override
    public int getOrder() {
        return GatewayConstants.FilterOrder.TRACE_FILTER + 50;
    }
}
