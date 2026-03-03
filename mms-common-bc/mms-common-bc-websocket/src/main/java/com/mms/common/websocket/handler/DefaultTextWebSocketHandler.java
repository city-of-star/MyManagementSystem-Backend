package com.mms.common.websocket.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mms.common.websocket.session.WebSocketSender;
import com.mms.common.websocket.session.WebSocketSessionRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * 实现功能【默认 WebSocket Handler】
 * <p>
 * - 注册/注销 session
 * - 支持 ping/pong
 * - 作为兜底示例：业务侧可自定义 Bean 覆盖掉（同名 bean：mmsWebSocketHandler）
 *
 * @author li.hongyu
 * @date 2026-03-03
 */
@Slf4j
public class DefaultTextWebSocketHandler extends TextWebSocketHandler {

    private final WebSocketSessionRegistry registry;
    private final WebSocketSender sender;
    private final ObjectMapper objectMapper;

    public DefaultTextWebSocketHandler(
            WebSocketSessionRegistry registry,
            WebSocketSender sender,
            ObjectMapper objectMapper) {
        this.registry = registry;
        this.sender = sender;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        registry.register(session);
        sender.sendTextToSession(session.getId(), "connected");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload() == null ? "" : message.getPayload().trim();
        if (payload.equalsIgnoreCase("ping")) {
            sender.sendTextToSession(session.getId(), "pong");
            return;
        }

        // 支持简单 JSON：{"type":"ping"}
        if (objectMapper != null && payload.startsWith("{") && payload.endsWith("}")) {
            try {
                JsonNode node = objectMapper.readTree(payload);
                String type = node.hasNonNull("type") ? node.get("type").asText() : null;
                if ("ping".equalsIgnoreCase(type)) {
                    sender.sendTextToSession(session.getId(), "{\"type\":\"pong\"}");
                    return;
                }
            } catch (Exception ignore) {
                // ignore json parse error, fallback echo
            }
        }

        // 默认行为：echo 回去，方便联调
        sender.sendTextToSession(session.getId(), payload);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        registry.unregister(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.debug("WebSocket传输出错：sessionId={}, err={}", session != null ? session.getId() : null, exception.getMessage());
    }
}

