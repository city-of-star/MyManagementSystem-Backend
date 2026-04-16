package com.mms.common.websocket.receive.dispatcher;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mms.common.websocket.common.constants.WebSocketConstants;
import com.mms.common.websocket.common.protocol.WsMessage;
import com.mms.common.websocket.receive.handler.WsReceiverMessageHandler;
import com.mms.common.websocket.common.session.WsSessionPrincipal;
import com.mms.common.websocket.registry.service.WsRegistryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 实现功能【WebSocket 文本处理器】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-03-27 10:00:00
 */
@Slf4j
public class WsReceiveTextDispatcher extends TextWebSocketHandler {

    private final WsRegistryService sessionRegistry;
    private final ObjectMapper objectMapper;
    private final Map<String, WsReceiverMessageHandler> handlersByType;

    public WsReceiveTextDispatcher(WsRegistryService sessionRegistry, ObjectMapper objectMapper, List<WsReceiverMessageHandler> messageHandlers) {
        this.sessionRegistry = sessionRegistry;
        this.objectMapper = objectMapper;
        this.handlersByType = buildHandlerMap(messageHandlers);
    }

    /**
     * 构建消息处理器映射表（type → handler）
     */
    private static Map<String, WsReceiverMessageHandler> buildHandlerMap(List<WsReceiverMessageHandler> messageHandlers) {
        if (messageHandlers == null || messageHandlers.isEmpty()) {
            return Map.of();
        }
        Map<String, WsReceiverMessageHandler> map = new LinkedHashMap<>();
        for (WsReceiverMessageHandler h : messageHandlers) {
            if (h == null) {
                continue;
            }
            String type = h.supportType();
            if (type == null || type.isBlank()) {
                log.warn("跳过 supportType 为空的 WsReceiveTextDispatcher: {}", h.getClass().getName());
                continue;
            }
            WsReceiverMessageHandler existing = map.putIfAbsent(type, h);
            if (existing != null) {
                log.warn("重复的 WsReceiveTextDispatcher: type={}, 保留 {}, 忽略 {}", type, existing.getClass().getName(), h.getClass().getName());
            }
        }
        return Map.copyOf(map);
    }

    /**
     * 连接成功
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Object userIdAttr = session.getAttributes().get(WebSocketConstants.WS_USER_ID);
        String userId = userIdAttr == null ? null : String.valueOf(userIdAttr);
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
        WsMessage<JsonNode> wsMessage = objectMapper.readValue(message.getPayload(), new TypeReference<WsMessage<JsonNode>>() {});
        String type = wsMessage != null && wsMessage.getType() != null ? wsMessage.getType() : "";
        WsReceiverMessageHandler handler = handlersByType.get(type);
        if (handler != null) {
            handler.handle(session, wsMessage);
        } else {
            log.debug("未定义的消息类型，type={}, sessionId={}", type, session.getId());
        }
    }
}
