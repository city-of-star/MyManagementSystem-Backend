package com.mms.common.websocket.interceptor;

import com.mms.common.websocket.constants.WebSocketConstants;
import com.mms.common.websocket.properties.WebSocketProperties;
import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * 实现功能【WebSocket 握手鉴权拦截器】
 * <p>
 * 在握手阶段提取 userId 并放入 attributes，供后续 handler 使用
 * </p>
 *
 * @author li.hongyu
 * @date 2026-03-26 16:28:47
 */
@NonNullApi
@AllArgsConstructor
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    private final WebSocketProperties properties;

    /**
     * 握手前处理
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            return !properties.isAuthEnabled();
        }
        HttpServletRequest rawRequest = servletRequest.getServletRequest();
        // 从请求头中获取 userId 请求头
        String userId = rawRequest.getHeader(properties.getUserIdHeader());
        // userId 为空则拒绝握手
        if (properties.isAuthEnabled() && (userId == null || userId.isBlank())) {
            return false;
        }
        // 将 userId 写入 attributes
        if (userId != null && !userId.isBlank()) {
            attributes.put(WebSocketConstants.ATTR_USER_ID, userId);
        }
        return true;
    }

    /**
     * 握手完成后回调
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, @Nullable Exception exception) {
        // no-op
    }
}

