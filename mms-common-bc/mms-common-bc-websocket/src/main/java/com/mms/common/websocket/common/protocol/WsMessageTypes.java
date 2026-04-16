package com.mms.common.websocket.common.protocol;

/**
 * 实现功能【WebSocket 消息类型】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-04-16 14:22:43
 */
public class WsMessageTypes {
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
    private WsMessageTypes() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}