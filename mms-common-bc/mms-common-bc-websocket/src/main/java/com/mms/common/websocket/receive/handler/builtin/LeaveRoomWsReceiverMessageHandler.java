package com.mms.common.websocket.receive.handler.builtin;

import com.fasterxml.jackson.databind.JsonNode;
import com.mms.common.websocket.receive.handler.WsReceiverMessageHandler;
import com.mms.common.websocket.auth.WsMessageRoomSupport;
import com.mms.common.websocket.common.protocol.WsMessage;
import com.mms.common.websocket.common.protocol.WsMessageTypes;
import com.mms.common.websocket.registry.service.WsRegistryService;
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
public class LeaveRoomWsReceiverMessageHandler implements WsReceiverMessageHandler {

    private final WsRegistryService sessionRegistry;

    @Override
    public String supportType() {
        return WsMessageTypes.TYPE_LEAVE_ROOM;
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

