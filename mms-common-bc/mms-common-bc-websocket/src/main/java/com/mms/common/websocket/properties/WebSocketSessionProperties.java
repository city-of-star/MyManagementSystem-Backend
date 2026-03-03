package com.mms.common.websocket.properties;

import lombok.Data;

/**
 * 实现功能【WebSocket Session 相关配置属性】
 *
 * @author li.hongyu
 * @date 2026-03-03
 */
@Data
public class WebSocketSessionProperties {

    /**
     * 握手 attributes / session attributes 中存放 userId 的 key
     */
    private String userIdKey = "userId";

    /**
     * 握手 attributes / session attributes 中存放 username 的 key
     */
    private String usernameKey = "username";
}

