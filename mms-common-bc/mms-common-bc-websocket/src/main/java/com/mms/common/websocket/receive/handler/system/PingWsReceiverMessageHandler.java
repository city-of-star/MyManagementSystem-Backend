package com.mms.common.websocket.receive.handler.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mms.common.core.utils.DateUtils;
import com.mms.common.websocket.receive.handler.WsReceiverMessageHandler;
import com.mms.common.websocket.common.protocol.WsMessage;
import com.mms.common.websocket.common.protocol.WsMessageTypes;
import com.mms.common.websocket.receive.handler.dto.EmptyDto;
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
public class PingWsReceiverMessageHandler implements WsReceiverMessageHandler<EmptyDto> {

    private final ObjectMapper objectMapper;

    @Override
    public String getMessageType() {
        return WsMessageTypes.TYPE_PING;
    }

    @Override
    public Class<EmptyDto> getDtoClass() {
        return EmptyDto.class;
    }

    @Override
    public void handle(WebSocketSession session, WsMessage<EmptyDto> message) {
        WsMessage<Map<String, Object>> pongMessage = WsMessage.<Map<String, Object>>builder()
                .type(WsMessageTypes.TYPE_PONG)
                .data(Map.of())
                .requestId(message != null ? message.getRequestId() : null)
                .timestamp(DateUtils.nowMillis())
                .build();
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(pongMessage)));
        } catch (IOException e) {
            log.warn("WebSocket pong 失败, sessionId={}", session.getId(), e);
        }
    }
}

