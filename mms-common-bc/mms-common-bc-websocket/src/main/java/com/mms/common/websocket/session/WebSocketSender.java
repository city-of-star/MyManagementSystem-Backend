package com.mms.common.websocket.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * 实现功能【WebSocket 消息发送工具】
 * <p>
 * 提供：
 * - 按 sessionId 推送
 * - 按 userKey(userId/username) 推送
 * - 广播推送
 *
 * @author li.hongyu
 * @date 2026-03-03
 */
@Slf4j
public class WebSocketSender {

    private final WebSocketSessionRegistry registry;
    private final ObjectMapper objectMapper;

    public WebSocketSender(WebSocketSessionRegistry registry, ObjectMapper objectMapper) {
        this.registry = registry;
        this.objectMapper = objectMapper;
    }

    /**
     * 向某个 session 推送文本
     */
    public boolean sendTextToSession(String sessionId, String text) {
        if (!StringUtils.hasText(sessionId)) {
            return false;
        }
        WebSocketSession session = registry.getSession(sessionId);
        return sendText(session, text);
    }

    /**
     * 向某个 userKey 推送文本（多端登录则多连接都会收到）
     *
     * @return 推送成功的连接数
     */
    public int sendTextToUser(String userKey, String text) {
        if (!StringUtils.hasText(userKey)) {
            return 0;
        }
        return (int) registry.getSessionsByUserKey(userKey).stream()
                .filter(s -> sendText(s, text))
                .count();
    }

    /**
     * 广播文本
     *
     * @return 推送成功的连接数
     */
    public int broadcastText(String text) {
        return (int) registry.getAllSessions().stream()
                .filter(s -> sendText(s, text))
                .count();
    }

    /**
     * 向某个 userKey 推送 JSON（依赖 ObjectMapper）
     */
    public int sendJsonToUser(String userKey, Object payload) {
        if (objectMapper == null) {
            throw new IllegalStateException("ObjectMapper Bean not found");
        }
        try {
            return sendTextToUser(userKey, objectMapper.writeValueAsString(payload));
        } catch (Exception e) {
            log.warn("WebSocket发送JSON失败：{}", e.getMessage());
            return 0;
        }
    }

    private boolean sendText(WebSocketSession session, String text) {
        if (session == null || !session.isOpen()) {
            return false;
        }
        try {
            session.sendMessage(new TextMessage(text == null ? "" : text));
            return true;
        } catch (IOException e) {
            log.warn("WebSocket发送失败：sessionId={}, err={}", session.getId(), e.getMessage());
            return false;
        }
    }
}

