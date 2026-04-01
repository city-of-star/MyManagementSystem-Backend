package com.mms.common.websocket.session;

import org.springframework.web.socket.WebSocketSession;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实现功能【内存版 WebSocket 会话注册表】
 * <p>
 * 单机模式默认实现，后续可按相同接口替换为分布式实现
 * </p>
 *
 * @author li.hongyu
 * @date 2026-03-26 16:28:47
 */
public class InMemoryWsSessionRegistry implements WsSessionRegistry {

    /**
     * sessionId → 连接
     */
    private final ConcurrentHashMap<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
    /**
     * userId → 该用户下所有连接
     */
    private final ConcurrentHashMap<String, Set<WebSocketSession>> userSessions = new ConcurrentHashMap<>();
    /**
     * roomId → 房间内所有连接
     */
    private final ConcurrentHashMap<String, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    /**
     * 注册会话
     */
    @Override
    public void register(WebSocketSession session, WsSessionPrincipal principal) {
        sessionMap.put(session.getId(), session);
        if (principal != null && principal.getUserId() != null && !principal.getUserId().isBlank()) {
            // 将 WebSocket 会话关联到指定用户
            userSessions.computeIfAbsent(principal.getUserId(), key -> ConcurrentHashMap.newKeySet()).add(session);
        }
    }

    /**
     * 移除会话
     */
    @Override
    public void unregister(WebSocketSession session) {
        sessionMap.remove(session.getId());
        userSessions.values().forEach(set -> set.remove(session));
        roomSessions.values().forEach(set -> set.remove(session));
    }

    /**
     * 根据 userId 获取此用户的所有会话
     */
    @Override
    public Set<WebSocketSession> getByUserId(String userId) {
        return userSessions.getOrDefault(userId, Collections.emptySet());
    }

    /**
     * 根据 roomId 获取此房间内的所有会话
     */
    @Override
    public Set<WebSocketSession> getByRoomId(String roomId) {
        return roomSessions.getOrDefault(roomId, Collections.emptySet());
    }

    /**
     * 获取所有会话
     */
    @Override
    public Set<WebSocketSession> getAllSessions() {
        return Set.copyOf(sessionMap.values());
    }

    /**
     * 将指定会话加入指定房间
     */
    @Override
    public void joinRoom(String roomId, WebSocketSession session) {
        if (roomId == null || roomId.isBlank()) {
            return;
        }
        roomSessions.computeIfAbsent(roomId, key -> ConcurrentHashMap.newKeySet()).add(session);
    }

    /**
     * 将指定会话移除指定房间
     */
    @Override
    public void leaveRoom(String roomId, WebSocketSession session) {
        Set<WebSocketSession> sessions = roomSessions.get(roomId);
        if (sessions == null) {
            return;
        }
        sessions.remove(session);
    }
}

