package com.mms.common.websocket.registry.event;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * WebSocket 会话注销完成事件。
 */
@Data
@AllArgsConstructor
public class WsSessionUnregisteredEvent {
    private final String sessionId;
    private final String userId;
}
