package com.mms.common.websocket.session;

import org.springframework.web.socket.WebSocketSession;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实现功能【内存版 WebSocket 会话注册表】
 * <p>
 * 单机模式默认实现，后续可按相同接口替换为分布式实现。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-03-26 16:28:47
 */
public class InMemoryWsSessionRegistry implements WsSessionRegistry {

    private final ConcurrentHashMap<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Set<WebSocketSession>> userSessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    @Override
    public void register(WebSocketSession session, WsSessionPrincipal principal) {
        sessionMap.put(session.getId(), session);
        if (principal != null && principal.getUserId() != null && !principal.getUserId().isBlank()) {
            userSessions.computeIfAbsent(principal.getUserId(), key -> ConcurrentHashMap.newKeySet()).add(session);
        }
    }

    @Override
    public void unregister(WebSocketSession session) {
        sessionMap.remove(session.getId());
        userSessions.values().forEach(set -> set.remove(session));
        roomSessions.values().forEach(set -> set.remove(session));
    }

    @Override
    public Set<WebSocketSession> getByUserId(String userId) {
        return userSessions.getOrDefault(userId, Collections.emptySet());
    }

    @Override
    public Set<WebSocketSession> getByRoomId(String roomId) {
        return roomSessions.getOrDefault(roomId, Collections.emptySet());
    }

    @Override
    public Set<WebSocketSession> getAllSessions() {
        return Set.copyOf(sessionMap.values());
    }

    @Override
    public void joinRoom(String roomId, WebSocketSession session) {
        if (roomId == null || roomId.isBlank()) {
            return;
        }
        roomSessions.computeIfAbsent(roomId, key -> ConcurrentHashMap.newKeySet()).add(session);
    }

    @Override
    public void leaveRoom(String roomId, WebSocketSession session) {
        Set<WebSocketSession> sessions = roomSessions.get(roomId);
        if (sessions == null) {
            return;
        }
        sessions.remove(session);
    }
}

