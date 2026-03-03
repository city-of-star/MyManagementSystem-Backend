package com.mms.common.websocket.auth;

/**
 * 实现功能【WebSocket 鉴权模式】
 *
 * @author li.hongyu
 * @date 2026-03-03
 */
public enum WebSocketAuthMode {
    /**
     * 信任网关透传头（X-User-Id / X-User-Name）
     */
    GATEWAY_HEADERS,
    /**
     * 握手阶段校验 JWT（Authorization 或 query param token）
     */
    JWT,
    /**
     * 不鉴权（不建议生产使用）
     */
    NONE
}

