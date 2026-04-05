package com.mms.common.websocket.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mms.common.websocket.protocol.WsMessage;
import com.mms.common.websocket.service.WsPushService;
import com.mms.common.websocket.service.WsRegistryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Set;

/**
 * 实现功能【WebSocket 推送服务实现类】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-03-26 16:28:47
 */
@Slf4j
@AllArgsConstructor
public class WsPushServiceImpl implements WsPushService {

    /**
     * WebSocket 会话注册表接口
     */
    private final WsRegistryService sessionRegistry;

    /**
     * JSON 序列化器
     */
    private final ObjectMapper objectMapper;

    /**
     * 推送给指定用户的所有连接
     */
    @Override
    public void pushToUser(String userId, WsMessage<?> message) {
        sendToSessions(sessionRegistry.getByUserId(userId), message);
    }

    /**
     * 推送给指定房间内所有已 join 的连接
     */
    @Override
    public void pushToRoom(String roomId, WsMessage<?> message) {
        sendToSessions(sessionRegistry.getByRoomId(roomId), message);
    }

    /**
     * 广播给当前注册表中的全部会话
     */
    @Override
    public void broadcast(WsMessage<?> message) {
        sendToSessions(sessionRegistry.getAllSessions(), message);
    }

    /**
     * 给指定会话集合发送消息
     */
    private void sendToSessions(Set<WebSocketSession> sessions, WsMessage<?> message) {
        if (sessions == null || sessions.isEmpty()) {
            return;
        }
        String payload;
        try {
            // 将消息转成 JSON字符串
            payload = objectMapper.writeValueAsString(message);
        } catch (IOException e) {
            log.error("WebSocket 消息序列化失败", e);
            return;
        }
        for (WebSocketSession session : sessions) {
            if (!session.isOpen()) {
                continue;
            }
            try {
                // 推送消息
                session.sendMessage(new TextMessage(payload));
            } catch (IOException e) {
                log.warn("WebSocket 消息推送失败, sessionId={}", session.getId(), e);
            }
        }
    }
}

