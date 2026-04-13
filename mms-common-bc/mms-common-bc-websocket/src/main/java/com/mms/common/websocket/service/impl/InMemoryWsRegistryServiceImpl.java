package com.mms.common.websocket.service.impl;

import com.mms.common.websocket.service.WsRegistryService;
import com.mms.common.websocket.session.WsSessionPrincipal;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
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
public class InMemoryWsRegistryServiceImpl implements WsRegistryService {

    /**
     * sessionId → 连接
     */
    private final ConcurrentHashMap<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
    /**
     * userId → 该用户下所有连接 sessionId
     */
    private final ConcurrentHashMap<String, Set<String>> userSessionIds = new ConcurrentHashMap<>();
    /**
     * roomId → 房间内所有连接 sessionId
     */
    private final ConcurrentHashMap<String, Set<String>> roomSessionIds = new ConcurrentHashMap<>();
    /**
     * sessionId → userId（反向索引，便于 unregister 快速清理）
     */
    private final ConcurrentHashMap<String, String> sessionUserId = new ConcurrentHashMap<>();
    /**
     * sessionId → rooms（反向索引，便于 unregister 快速清理）
     */
    private final ConcurrentHashMap<String, Set<String>> sessionRooms = new ConcurrentHashMap<>();

    /**
     * 单个会话的发送超时时间（毫秒）
     */
    private static final int SEND_TIME_LIMIT_MS = 10_000;
    /**
     * 单个会话的发送缓冲区大小（字节）
     */
    private static final int SEND_BUFFER_SIZE_BYTES = 512 * 1024;

    /**
     * 注册会话
     */
    @Override
    public void register(WebSocketSession session, WsSessionPrincipal principal) {
        String sessionId = session.getId();
        WebSocketSession safeSession = decorate(session);
        sessionMap.put(sessionId, safeSession);

        // 初始化反向索引容器
        sessionRooms.computeIfAbsent(sessionId, key -> ConcurrentHashMap.newKeySet());

        if (principal != null && principal.getUserId() != null && !principal.getUserId().isBlank()) {
            String userId = principal.getUserId();
            sessionUserId.put(sessionId, userId);
            userSessionIds.computeIfAbsent(userId, key -> ConcurrentHashMap.newKeySet()).add(sessionId);
        }
    }

    /**
     * 移除会话
     */
    @Override
    public void unregister(WebSocketSession session) {
        String sessionId = session.getId();

        // 先移除主表
        sessionMap.remove(sessionId);

        // 清理 user 索引
        String userId = sessionUserId.remove(sessionId);
        if (userId != null && !userId.isBlank()) {
            userSessionIds.computeIfPresent(userId, (key, set) -> {
                set.remove(sessionId);
                return set.isEmpty() ? null : set;
            });
        }

        // 清理 room 索引
        Set<String> rooms = sessionRooms.remove(sessionId);
        if (rooms != null && !rooms.isEmpty()) {
            for (String roomId : rooms) {
                if (roomId == null || roomId.isBlank()) {
                    continue;
                }
                roomSessionIds.computeIfPresent(roomId, (key, set) -> {
                    set.remove(sessionId);
                    return set.isEmpty() ? null : set;
                });
            }
        }
    }

    /**
     * 根据 userId 获取此用户的所有会话
     */
    @Override
    public Set<WebSocketSession> getByUserId(String userId) {
        Set<String> ids = userSessionIds.get(userId);
        if (ids == null || ids.isEmpty()) {
            return Collections.emptySet();
        }
        Set<WebSocketSession> result = new HashSet<>();
        for (String sessionId : ids) {
            WebSocketSession session = sessionMap.get(sessionId);
            if (session != null) {
                result.add(session);
            }
        }
        return result;
    }

    /**
     * 根据 roomId 获取此房间内的所有会话
     */
    @Override
    public Set<WebSocketSession> getByRoomId(String roomId) {
        Set<String> ids = roomSessionIds.get(roomId);
        if (ids == null || ids.isEmpty()) {
            return Collections.emptySet();
        }
        Set<WebSocketSession> result = new HashSet<>();
        for (String sessionId : ids) {
            WebSocketSession session = sessionMap.get(sessionId);
            if (session != null) {
                result.add(session);
            }
        }
        return result;
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
        String sessionId = session.getId();
        // 先记录反向索引，保证 unregister 能快速清理
        sessionRooms.computeIfAbsent(sessionId, key -> ConcurrentHashMap.newKeySet()).add(roomId);
        roomSessionIds.computeIfAbsent(roomId, key -> ConcurrentHashMap.newKeySet()).add(sessionId);
    }

    /**
     * 将指定会话移除指定房间
     */
    @Override
    public void leaveRoom(String roomId, WebSocketSession session) {
        if (roomId == null || roomId.isBlank()) {
            return;
        }
        String sessionId = session.getId();

        // room → sessionIds
        roomSessionIds.computeIfPresent(roomId, (key, set) -> {
            set.remove(sessionId);
            return set.isEmpty() ? null : set;
        });

        // sessionId → rooms
        sessionRooms.computeIfPresent(sessionId, (key, set) -> {
            set.remove(roomId);
            return set;
        });
    }

    private WebSocketSession decorate(WebSocketSession session) {
        if (session instanceof ConcurrentWebSocketSessionDecorator) {
            return session;
        }
        return new ConcurrentWebSocketSessionDecorator(session, SEND_TIME_LIMIT_MS, SEND_BUFFER_SIZE_BYTES);
    }
}

