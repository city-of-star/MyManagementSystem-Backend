package com.mms.common.websocket.handler.builtin;

import com.fasterxml.jackson.databind.JsonNode;
import com.mms.common.websocket.constants.WebSocketConstants;
import com.mms.common.websocket.handler.WsMessageHandler;
import com.mms.common.websocket.handler.WsMessageRoomSupport;
import com.mms.common.websocket.protocol.WsMessage;
import com.mms.common.websocket.service.WsRegistryService;
import lombok.AllArgsConstructor;
import org.springframework.web.socket.WebSocketSession;

/**
 * 实现功能【离开房间】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-04-15 10:15:26
 */
@AllArgsConstructor
public class LeaveRoomWsMessageHandler implements WsMessageHandler {

    private final WsRegistryService sessionRegistry;

    @Override
    public String supportType() {
        return WebSocketConstants.TYPE_LEAVE_ROOM;
    }

    @Override
    public void handle(WebSocketSession session, WsMessage<JsonNode> message) {
        JsonNode data = message != null ? message.getData() : null;
        String roomId = WsMessageRoomSupport.extractRoomId(data);
        if (roomId != null) {
            sessionRegistry.leaveRoom(roomId, session);
        }
    }
}

