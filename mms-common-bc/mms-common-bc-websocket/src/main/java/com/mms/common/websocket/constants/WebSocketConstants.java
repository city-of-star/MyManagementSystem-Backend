package com.mms.common.websocket.constants;

import com.mms.common.core.constants.gateway.GatewayConstants;

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

    /**
     *  WebSocket 路径
     */
    public static final String DEFAULT_ENDPOINT = "/ws";
    /**
     * 用户ID请求头（网关透传）
     */
    public static final String DEFAULT_USER_ID_HEADER = GatewayConstants.Headers.USER_ID;
    /**
     * 用户 ID
     */
    public static final String ATTR_USER_ID = "ws_user_id";
    /**
     * 会话 ID
     */
    public static final String ATTR_SESSION_ID = "ws_session_id";
    /**
     * 消息类型-ping
     */
    public static final String TYPE_PING = "ping";
    /**
     * 消息类型-pong
     */
    public static final String TYPE_PONG = "pong";
    /**
     * 消息类型-加入房间
     */
    public static final String TYPE_JOIN_ROOM = "join_room";
    /**
     * 消息类型-离开房间
     */
    public static final String TYPE_LEAVE_ROOM = "leave_room";

    /**
     * 私有构造函数，防止实例化
     */
    private WebSocketConstants() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}

