package com.mms.common.websocket.registry.service.impl;

import com.mms.common.websocket.common.properties.WebSocketProperties;
import com.mms.common.websocket.registry.service.WsRegistryService;
import com.mms.common.websocket.registry.listener.WsRegistryListener;
import com.mms.common.websocket.common.session.WsSessionPrincipal;
import lombok.AllArgsConstructor;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Collections;
import java.util.List;
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
@AllArgsConstructor
public class InMemoryWsRegistryServiceImpl implements WsRegistryService {

    private final WebSocketProperties properties;
    private final List<WsRegistryListener> listeners;

    /**
     * 全局会话注册表（sessionId → 会话）
     */
    private final ConcurrentHashMap<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
    /**
     * 用户会话注册表（userId → 该用户下所有连接 sessionId）
     */
    private final ConcurrentHashMap<String, Set<String>> userSessionIds = new ConcurrentHashMap<>();
    /**
     * 房间会话注册表（roomId → 房间内所有连接 sessionId）
     */
    private final ConcurrentHashMap<String, Set<String>> roomSessionIds = new ConcurrentHashMap<>();
    /**
     * 反向索引（sessionId → userId）
     */
    private final ConcurrentHashMap<String, String> sessionUserId = new ConcurrentHashMap<>();
    /**
     * 反向索引（sessionId → rooms）
     */
    private final ConcurrentHashMap<String, Set<String>> sessionRooms = new ConcurrentHashMap<>();

    /**
     * 注册会话
     */
    @Override
    public void register(WebSocketSession session, WsSessionPrincipal principal) {
        // 获取 sessionId
        String sessionId = session.getId();
        // 将原始 WebSocketSession 装饰成安全的会话
        WebSocketSession safeSession = decorateSafeSession(session);
        // 注册会话
        sessionMap.put(sessionId, safeSession);
        if (principal != null && principal.getUserId() != null && !principal.getUserId().isBlank()) {
            // 获取用户ID
            String userId = principal.getUserId();
            // 将会话注册到用户
            userSessionIds.computeIfAbsent(userId, key -> ConcurrentHashMap.newKeySet()).add(sessionId);
            // 添加反向索引
            sessionUserId.put(sessionId, userId);
            sessionRooms.computeIfAbsent(sessionId, key -> ConcurrentHashMap.newKeySet());
        }
        // 执行监听器的注册会话后动作（业务逻辑）
        if (listeners != null && !listeners.isEmpty()) {
            for (WsRegistryListener listener : listeners) {
                try {
                    listener.onRegistered(safeSession, principal);
                } catch (Exception ignored) {
                    // 忽略监听器异常避免破坏 WebSocket 核心流程
                }
            }
        }
    }

    /**
     * 移除会话
     */
    @Override
    public void unregister(WebSocketSession session) {
        // 获取 sessionId
        String sessionId = session.getId();
        // 获取 user 反向索引
        String userIdForCallback = sessionUserId.get(sessionId);
        // 移除会话
        sessionMap.remove(sessionId);
        // 清理 user 反向索引
        String userId = sessionUserId.remove(sessionId);
        // 清理用户会话注册表
        if (userId != null && !userId.isBlank()) {
            userSessionIds.computeIfPresent(userId, (key, set) -> {
                set.remove(sessionId);
                return set.isEmpty() ? null : set;
            });
        }
        // 清理 room 反向索引
        Set<String> rooms = sessionRooms.remove(sessionId);
        // 清理房间会话注册表
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
        // 执行监听器的移除会话后动作（业务逻辑）
        if (listeners != null && !listeners.isEmpty()) {
            for (WsRegistryListener listener : listeners) {
                try {
                    listener.onUnregistered(sessionId, userIdForCallback);
                } catch (Exception ignored) {
                    // 忽略监听器异常避免破坏 WebSocket 核心流程
                }
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
        // 添加反向索引
        sessionRooms.computeIfAbsent(sessionId, key -> ConcurrentHashMap.newKeySet()).add(roomId);
        // 将会话注册到房间
        roomSessionIds.computeIfAbsent(roomId, key -> ConcurrentHashMap.newKeySet()).add(sessionId);
        // 执行监听器的加入房间后动作（业务逻辑）
        if (listeners != null && !listeners.isEmpty()) {
            String userId = sessionUserId.get(sessionId);
            for (WsRegistryListener listener : listeners) {
                try {
                    listener.onRoomJoined(roomId, sessionId, userId);
                } catch (Exception ignored) {
                    // 忽略监听器异常避免破坏 WebSocket 核心流程
                }
            }
        }
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
        // 从该房间移除这个会话
        roomSessionIds.computeIfPresent(roomId, (key, set) -> {
            set.remove(sessionId);
            return set.isEmpty() ? null : set;
        });
        // 清理 room 反向索引
        sessionRooms.computeIfPresent(sessionId, (key, set) -> {
            set.remove(roomId);
            return set.isEmpty() ? null : set;
        });
        // 执行监听器的移除房间后动作（业务逻辑）
        if (listeners != null && !listeners.isEmpty()) {
            String userId = sessionUserId.get(sessionId);
            for (WsRegistryListener listener : listeners) {
                try {
                    listener.onRoomLeft(roomId, sessionId, userId);
                } catch (Exception ignored) {
                    // 忽略监听器异常避免破坏 WebSocket 核心流程
                }
            }
        }
    }

    /**
     * 装饰原始 WebSocketSession 类型，变成安全的 ConcurrentWebSocketSessionDecorator
     */
    private WebSocketSession decorateSafeSession(WebSocketSession session) {
        if (session instanceof ConcurrentWebSocketSessionDecorator) {
            return session;
        }
        return new ConcurrentWebSocketSessionDecorator(session, properties.getSendTimeLimitMs(), properties.getSendBufferSizeBytes());
    }
}

