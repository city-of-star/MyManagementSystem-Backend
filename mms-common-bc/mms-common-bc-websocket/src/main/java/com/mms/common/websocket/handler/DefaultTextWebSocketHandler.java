package com.mms.common.websocket.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mms.common.websocket.constants.WebSocketConstants;
import com.mms.common.websocket.protocol.WsMessage;
import com.mms.common.websocket.session.WsSessionPrincipal;
import com.mms.common.websocket.session.WsSessionRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;

/**
 * 实现功能【默认 WebSocket 文本处理器】
 * <p>
 * 负责基础协议处理：连接注册、心跳、房间加入/退出。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-03-27 10:00:00
 */
@Slf4j
public class DefaultTextWebSocketHandler extends TextWebSocketHandler {

    private final WsSessionRegistry sessionRegistry;
    private final ObjectMapper objectMapper;

    public DefaultTextWebSocketHandler(WsSessionRegistry sessionRegistry, ObjectMapper objectMapper) {
        this.sessionRegistry = sessionRegistry;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Object userIdAttr = session.getAttributes().get(WebSocketConstants.ATTR_USER_ID);
        String userId = userIdAttr == null ? null : String.valueOf(userIdAttr);
        sessionRegistry.register(session, WsSessionPrincipal.builder()
                .sessionId(session.getId())
                .userId(userId)
                .build());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonNode root = objectMapper.readTree(message.getPayload());
        String type = root.path("type").asText("");
        JsonNode dataNode = root.path("data");

        switch (type) {
            case WebSocketConstants.TYPE_PING -> sendPong(session, root.path("requestId").asText(null));
            case WebSocketConstants.TYPE_JOIN_ROOM -> {
                String roomId = readRoomId(dataNode);
                if (roomId != null) {
                    sessionRegistry.joinRoom(roomId, session);
                }
            }
            case WebSocketConstants.TYPE_LEAVE_ROOM -> {
                String roomId = readRoomId(dataNode);
                if (roomId != null) {
                    sessionRegistry.leaveRoom(roomId, session);
                }
            }
            default -> log.debug("Ignore unsupported websocket message type={}, sessionId={}", type, session.getId());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionRegistry.unregister(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.warn("WebSocket transport error, sessionId={}", session.getId(), exception);
    }

    private String readRoomId(JsonNode dataNode) {
        if (dataNode == null || dataNode.isMissingNode() || dataNode.isNull()) {
            return null;
        }
        if (dataNode.isTextual()) {
            return dataNode.asText();
        }
        JsonNode roomIdNode = dataNode.path("roomId");
        if (roomIdNode.isTextual()) {
            return roomIdNode.asText();
        }
        return null;
    }

    private void sendPong(WebSocketSession session, String requestId) {
        WsMessage<Map<String, Object>> pongMessage = WsMessage.<Map<String, Object>>builder()
                .type(WebSocketConstants.TYPE_PONG)
                .requestId(requestId)
                .timestamp(System.currentTimeMillis())
                .data(Map.of())
                .build();
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(pongMessage)));
        } catch (IOException e) {
            log.warn("WebSocket send pong failed, sessionId={}", session.getId(), e);
        }
    }
}
