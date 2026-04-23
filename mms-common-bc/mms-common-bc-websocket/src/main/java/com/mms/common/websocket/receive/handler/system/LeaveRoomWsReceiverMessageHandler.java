package com.mms.common.websocket.receive.handler.system;

import com.mms.common.websocket.receive.handler.WsReceiverMessageHandler;
import com.mms.common.websocket.common.protocol.WsMessage;
import com.mms.common.websocket.common.protocol.WsMessageTypes;
import com.mms.common.websocket.receive.handler.dto.RoomActionDto;
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
public class LeaveRoomWsReceiverMessageHandler implements WsReceiverMessageHandler<RoomActionDto> {

    private final WsRegistryService sessionRegistry;

    @Override
    public String getMessageType() {
        return WsMessageTypes.TYPE_LEAVE_ROOM;
    }

    @Override
    public Class<RoomActionDto> getDtoClass() {
        return RoomActionDto.class;
    }

    @Override
    public void handle(WebSocketSession session, WsMessage<RoomActionDto> message) {
        RoomActionDto data = message != null ? message.getData() : null;
        String roomId = data != null ? data.getRoomId() : null;
        if (roomId != null && !roomId.isBlank()) {
            sessionRegistry.leaveRoom(roomId, session);
        }
    }
}

