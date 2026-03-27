package com.mms.common.websocket.constants;

/**
 * 实现功能【WebSocket 常量定义】
 * <p>
 * 统一维护模块内部常量，避免魔法值散落在各个类中。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-03-26 16:28:47
 */
public final class WebSocketConstants {

    private WebSocketConstants() {
    }

    public static final String DEFAULT_ENDPOINT = "/ws";
    public static final String DEFAULT_USER_ID_HEADER = "X-User-Id";

    public static final String ATTR_USER_ID = "ws_user_id";
    public static final String ATTR_SESSION_ID = "ws_session_id";

    public static final String TYPE_PING = "ping";
    public static final String TYPE_PONG = "pong";
    public static final String TYPE_JOIN_ROOM = "join_room";
    public static final String TYPE_LEAVE_ROOM = "leave_room";
}

