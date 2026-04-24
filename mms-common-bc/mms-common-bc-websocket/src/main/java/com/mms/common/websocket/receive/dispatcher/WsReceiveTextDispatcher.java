package com.mms.common.websocket.receive.dispatcher;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mms.common.websocket.common.constants.WebSocketConstants;
import com.mms.common.websocket.common.protocol.WsMessage;
import com.mms.common.websocket.receive.handler.WsReceiverMessageHandler;
import com.mms.common.websocket.receive.router.WsReceiveRouter;
import com.mms.common.websocket.common.session.WsSessionPrincipal;
import com.mms.common.websocket.registry.service.WsRegistryService;
import jakarta.validation.Validator;
import io.micrometer.common.lang.NonNullApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;

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
@NonNullApi
public class WsReceiveTextDispatcher extends TextWebSocketHandler {

    private final WsRegistryService sessionRegistry;
    private final ObjectMapper objectMapper;
    private final WsReceiveRouter router;

    public WsReceiveTextDispatcher(WsRegistryService sessionRegistry, ObjectMapper objectMapper, Validator validator, List<WsReceiverMessageHandler<?>> messageHandlers) {
        this.sessionRegistry = sessionRegistry;
        this.objectMapper = objectMapper;
        this.router = new WsReceiveRouter(objectMapper, validator, messageHandlers);
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
        // 从原始 ws 消息中获取 WsMessage
        WsMessage<JsonNode> wsMessage = objectMapper.readValue(message.getPayload(), new TypeReference<>() {});
        router.dispatch(session, wsMessage);
    }
}
