package com.mms.common.websocket.common.constants;

/**
 * 实现功能【WebSocket 常量定义】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-03-26 16:28:47
 */
public final class WebSocketConstants {
    /**
     * WebSocket 用户 ID
     */
    public static final String WS_USER_ID = "ws_user_id";
    /**
     * WebSocket 会话 ID
     */
    public static final String WS_SESSION_ID = "ws_session_id";
    /**
     * 私有构造函数，防止实例化
     */
    private WebSocketConstants() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}

