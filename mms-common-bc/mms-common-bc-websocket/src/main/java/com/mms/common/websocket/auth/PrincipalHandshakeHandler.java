package com.mms.common.websocket.auth;

import com.mms.common.websocket.properties.WebSocketProperties;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

/**
 * 实现功能【WebSocket 握手阶段绑定 Principal】
 *
 * @author li.hongyu
 * @date 2026-03-03
 */
public class PrincipalHandshakeHandler extends DefaultHandshakeHandler {

    private final WebSocketProperties properties;

    public PrincipalHandshakeHandler(WebSocketProperties properties) {
        this.properties = properties;
    }

    @Override
    protected @Nullable Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String userIdKey = properties.getSession().getUserIdKey();
        String usernameKey = properties.getSession().getUsernameKey();
        Object userId = attributes.get(userIdKey);
        Object username = attributes.get(usernameKey);
        if (userId == null && username == null) {
            return null;
        }
        return new WebSocketPrincipal(
                userId != null ? userId.toString() : null,
                username != null ? username.toString() : null
        );
    }
}

