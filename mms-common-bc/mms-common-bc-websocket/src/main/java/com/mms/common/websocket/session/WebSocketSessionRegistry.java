package com.mms.common.websocket.session;

import com.mms.common.websocket.auth.WebSocketPrincipal;
import com.mms.common.websocket.properties.WebSocketProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实现功能【WebSocket Session 注册表】
 * <p>
 * 负责维护：
 * - sessionId -> WebSocketSession
 * - userKey(userId/username) -> sessionId 集合
 *
 * @author li.hongyu
 * @date 2026-03-03
 */
@Slf4j
public class WebSocketSessionRegistry {

    private final WebSocketProperties properties;

    private final Map<String, WebSocketSession> sessionsById = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> sessionIdsByUserKey = new ConcurrentHashMap<>();

    public WebSocketSessionRegistry(WebSocketProperties properties) {
        this.properties = properties;
    }

    public void register(WebSocketSession session) {
        if (session == null) {
            return;
        }
        sessionsById.put(session.getId(), session);
        String userKey = resolveUserKey(session);
        if (StringUtils.hasText(userKey)) {
            sessionIdsByUserKey.computeIfAbsent(userKey, k -> ConcurrentHashMap.newKeySet()).add(session.getId());
        }
        log.debug("WebSocket注册session：sessionId={}, userKey={}", session.getId(), userKey);
    }

    public void unregister(WebSocketSession session) {
        if (session == null) {
            return;
        }
        sessionsById.remove(session.getId());
        String userKey = resolveUserKey(session);
        if (StringUtils.hasText(userKey)) {
            Set<String> ids = sessionIdsByUserKey.getOrDefault(userKey, Collections.emptySet());
            ids.remove(session.getId());
            if (ids.isEmpty()) {
                sessionIdsByUserKey.remove(userKey);
            }
        }
        log.debug("WebSocket注销session：sessionId={}, userKey={}", session.getId(), userKey);
    }

    public WebSocketSession getSession(String sessionId) {
        return sessionsById.get(sessionId);
    }

    public Collection<WebSocketSession> getSessionsByUserKey(String userKey) {
        if (!StringUtils.hasText(userKey)) {
            return Collections.emptyList();
        }
        Set<String> ids = sessionIdsByUserKey.getOrDefault(userKey, Collections.emptySet());
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        return ids.stream()
                .map(sessionsById::get)
                .filter(s -> s != null && s.isOpen())
                .toList();
    }

    public Collection<WebSocketSession> getAllSessions() {
        return sessionsById.values().stream()
                .filter(s -> s != null && s.isOpen())
                .toList();
    }

    public int sessionCount() {
        return sessionsById.size();
    }

    private String resolveUserKey(WebSocketSession session) {
        // 1) 优先从 Principal 获取
        if (session.getPrincipal() instanceof WebSocketPrincipal p) {
            if (StringUtils.hasText(p.getUserId())) {
                return p.getUserId();
            }
            if (StringUtils.hasText(p.getUsername())) {
                return p.getUsername();
            }
        } else if (session.getPrincipal() != null && StringUtils.hasText(session.getPrincipal().getName())) {
            return session.getPrincipal().getName();
        }

        // 2) 兜底从握手 attributes 获取
        String userIdKey = properties.getSession().getUserIdKey();
        String usernameKey = properties.getSession().getUsernameKey();
        Object userId = session.getAttributes().get(userIdKey);
        if (userId != null && StringUtils.hasText(userId.toString())) {
            return userId.toString();
        }
        Object username = session.getAttributes().get(usernameKey);
        if (username != null && StringUtils.hasText(username.toString())) {
            return username.toString();
        }
        return null;
    }
}

