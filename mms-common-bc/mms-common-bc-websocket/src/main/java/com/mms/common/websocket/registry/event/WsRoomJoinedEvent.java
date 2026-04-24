package com.mms.common.websocket.registry.event;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * WebSocket 会话加入房间完成事件。
 */
@Data
@AllArgsConstructor
public class WsRoomJoinedEvent {
    private final String roomId;
    private final String sessionId;
    private final String userId;
}
