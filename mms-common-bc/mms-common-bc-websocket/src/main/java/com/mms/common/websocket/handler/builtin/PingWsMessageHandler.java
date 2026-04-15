package com.mms.common.websocket.handler.builtin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mms.common.websocket.constants.WebSocketConstants;
import com.mms.common.websocket.handler.WsMessageHandler;
import com.mms.common.websocket.protocol.WsMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

/**
 * 实现功能【ping → pong】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-04-15 10:15:26
 */
@Slf4j
@AllArgsConstructor
public class PingWsMessageHandler implements WsMessageHandler {

    private final ObjectMapper objectMapper;

    @Override
    public String supportType() {
        return WebSocketConstants.TYPE_PING;
    }

    @Override
    public void handle(WebSocketSession session, WsMessage<JsonNode> message) {
        WsMessage<Map<String, Object>> pongMessage = WsMessage.<Map<String, Object>>builder()
                .type(WebSocketConstants.TYPE_PONG)
                .data(Map.of())
                .requestId(message != null ? message.getRequestId() : null)
                .timestamp(System.currentTimeMillis())
                .build();
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(pongMessage)));
        } catch (IOException e) {
            log.warn("WebSocket pong 失败, sessionId={}", session.getId(), e);
        }
    }
}

