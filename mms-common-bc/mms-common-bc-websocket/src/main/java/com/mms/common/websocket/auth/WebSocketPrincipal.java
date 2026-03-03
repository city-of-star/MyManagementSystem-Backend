package com.mms.common.websocket.auth;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.security.Principal;
import java.util.Objects;

/**
 * 实现功能【WebSocket Principal】
 * <p>
 * 绑定 userId / username，便于：
 * - session registry 定向推送
 * - 业务侧在 handler 内直接获取登录人信息
 *
 * @author li.hongyu
 * @date 2026-03-03
 */
@Getter
public class WebSocketPrincipal implements Principal, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String userId;
    private final String username;

    public WebSocketPrincipal(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    @Override
    public String getName() {
        if (username != null && !username.isBlank()) {
            return username;
        }
        return userId != null ? userId : "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebSocketPrincipal that = (WebSocketPrincipal) o;
        return Objects.equals(userId, that.userId) && Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username);
    }

    @Override
    public String toString() {
        return "WebSocketPrincipal{userId='" + userId + "', username='" + username + "'}";
    }
}

