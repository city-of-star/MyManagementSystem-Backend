package com.mms.common.websocket.registry.event;

import com.mms.common.websocket.common.session.WsSessionPrincipal;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

/**
 * WebSocket 会话注册完成事件。
 */
@Data
@AllArgsConstructor
public class WsSessionRegisteredEvent {
    private final WebSocketSession session;
    private final WsSessionPrincipal principal;
}
