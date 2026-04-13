package com.mms.common.websocket.service;

import com.mms.common.websocket.session.WsSessionPrincipal;
import org.springframework.web.socket.WebSocketSession;

/**
 * WebSocket 会话注册表生命周期监听器。
 * <p>
 * 业务模块可通过实现该接口订阅连接注册/注销、房间加入/退出等事件，
 * 以避免通过继承默认 Registry 实现来插入业务逻辑。
 * </p>
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

