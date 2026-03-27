package com.mms.common.websocket.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mms.common.websocket.protocol.WsMessage;
import com.mms.common.websocket.service.WsPushService;
import com.mms.common.websocket.session.WsSessionRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Set;

/**
 * 实现功能【WebSocket 推送服务实现类】
 * <p>
 * 基于会话注册表进行路由并发送文本消息。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-03-26 16:28:47
 */
@Slf4j
public class WsPushServiceImpl implements WsPushService {

    private final WsSessionRegistry sessionRegistry;
    private final ObjectMapper objectMapper;

    public WsPushServiceImpl(WsSessionRegistry sessionRegistry, ObjectMapper objectMapper) {
        this.sessionRegistry = sessionRegistry;
        this.objectMapper = objectMapper;
    }

    @Override
    public void pushToUser(String userId, WsMessage<?> message) {
        sendToSessions(sessionRegistry.getByUserId(userId), message);
    }

    @Override
    public void pushToRoom(String roomId, WsMessage<?> message) {
        sendToSessions(sessionRegistry.getByRoomId(roomId), message);
    }

    @Override
    public void broadcast(WsMessage<?> message) {
        sendToSessions(sessionRegistry.getAllSessions(), message);
    }

    private void sendToSessions(Set<WebSocketSession> sessions, WsMessage<?> message) {
        if (sessions == null || sessions.isEmpty()) {
            return;
        }
        String payload;
        try {
            payload = objectMapper.writeValueAsString(message);
        } catch (IOException e) {
            log.error("WebSocket message serialization failed", e);
            return;
        }

        for (WebSocketSession session : sessions) {
            if (!session.isOpen()) {
                continue;
            }
            try {
                session.sendMessage(new TextMessage(payload));
            } catch (IOException e) {
                log.warn("WebSocket push failed, sessionId={}", session.getId(), e);
            }
        }
    }
}

