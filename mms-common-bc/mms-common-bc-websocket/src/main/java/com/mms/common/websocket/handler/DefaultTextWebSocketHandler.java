package com.mms.common.websocket.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mms.common.websocket.constants.WebSocketConstants;
import com.mms.common.websocket.protocol.WsMessage;
import com.mms.common.websocket.session.WsSessionPrincipal;
import com.mms.common.websocket.service.WsRegistryService;
import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.Nullable;
import lombok.AllArgsConstructor;
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
 * 负责基础协议处理：连接注册、心跳、房间加入/退出
 * </p>
 *
 * @author li.hongyu
 * @date 2026-03-27 10:00:00
 */
@Slf4j
@NonNullApi
@AllArgsConstructor
public class DefaultTextWebSocketHandler extends TextWebSocketHandler {

    /**
     * WebSocket 会话注册表接口
     */
    private final WsRegistryService sessionRegistry;

    /**
     * JSON 序列化器
     */
    private final ObjectMapper objectMapper;

    /**
     * 连接成功
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // 从 attributes 取出 userId
        Object userIdAttr = session.getAttributes().get(WebSocketConstants.WS_USER_ID);
        String userId = userIdAttr == null ? null : String.valueOf(userIdAttr);
        // 登记到注册表
        sessionRegistry.register(session, WsSessionPrincipal.builder()
                .sessionId(session.getId())
                .userId(userId)
                .build());
    }

    /**
     * 连接关闭
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionRegistry.unregister(session);
    }

    /**
     * 传输异常
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.warn("WebSocket 传输异常, sessionId={}", session.getId(), exception);
        try {
            sessionRegistry.unregister(session);
        } catch (Exception e) {
            log.debug("WebSocket 传输异常清理注册表失败, sessionId={}", session.getId(), e);
        }
    }

    /**
     * 处理文本消息
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 将消息转换成 WsMessage 类型
        WsMessage<JsonNode> wsMessage = objectMapper.readValue(message.getPayload(), new TypeReference<WsMessage<JsonNode>>() {});
        // 获取消息类型
        String type = wsMessage == null ? "" : String.valueOf(wsMessage.getType());
        // 根据消息类型来处理不同的场景
        switch (type) {
            // ping
            case WebSocketConstants.TYPE_PING -> sendPong(session, wsMessage.getRequestId());
            // 加入房间
            case WebSocketConstants.TYPE_JOIN_ROOM -> {
                // 获取房间ID
                String roomId = getRoomId(wsMessage.getData());
                if (roomId != null) {
                    sessionRegistry.joinRoom(roomId, session);
                }
            }
            // 离开房间
            case WebSocketConstants.TYPE_LEAVE_ROOM -> {
                String roomId = getRoomId(wsMessage.getData());
                if (roomId != null) {
                    sessionRegistry.leaveRoom(roomId, session);
                }
            }
            // 未定义的消息类型
            default -> log.debug("未定义的消息类型，type={}, sessionId={}", type, session.getId());
        }
    }

    /**
     * 从消息负载里解析出房间 ID
     */
    @Nullable
    private String getRoomId(@Nullable JsonNode dataNode) {
        if (dataNode == null || dataNode.isMissingNode() || dataNode.isNull()) {
            return null;
        }
        if (dataNode.isTextual()) {
            String roomId = dataNode.asText();
            return roomId.isBlank() ? null : roomId;
        }
        JsonNode roomIdNode = dataNode.path("roomId");
        if (roomIdNode.isTextual()) {
            String roomId = roomIdNode.asText();
            return roomId.isBlank() ? null : roomId;
        }
        return null;
    }

    /**
     * 构造 pong 文本帧
     */
    private void sendPong(WebSocketSession session, String requestId) {
        WsMessage<Map<String, Object>> pongMessage = WsMessage.<Map<String, Object>>builder()
                .type(WebSocketConstants.TYPE_PONG)
                .data(Map.of())
                .requestId(requestId)
                .timestamp(System.currentTimeMillis())
                .build();
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(pongMessage)));
        } catch (IOException e) {
            log.warn("WebSocket pong 失败, sessionId={}", session.getId(), e);
        }
    }
}
