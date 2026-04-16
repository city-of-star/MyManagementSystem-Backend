package com.mms.common.websocket.registry.listener;

import com.mms.common.websocket.common.session.WsSessionPrincipal;
import org.springframework.web.socket.WebSocketSession;

/**
 * 实现功能【WebSocket 会话注册表生命周期监听器】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-04-14 11:09:25
 */
public interface WsRegistryListener {

    /**
     * 会话注册完成后触发
     */
    default void onRegistered(WebSocketSession session, WsSessionPrincipal principal) {
        // no-op
    }

    /**
     * 会话注销完成后触发
     */
    default void onUnregistered(String sessionId, String userId) {
        // no-op
    }

    /**
     * 会话加入房间完成后触发
     */
    default void onRoomJoined(String roomId, String sessionId, String userId) {
        // no-op
    }

    /**
     * 会话离开房间完成后触发
     */
    default void onRoomLeft(String roomId, String sessionId, String userId) {
        // no-op
    }
}

