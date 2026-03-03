package com.mms.common.websocket.properties;

import com.mms.common.core.constants.gateway.GatewayConstants;
import com.mms.common.core.enums.jwt.TokenType;
import com.mms.common.security.constants.JwtHeaderConstants;
import com.mms.common.websocket.auth.WebSocketAuthMode;
import lombok.Data;

/**
 * 实现功能【WebSocket 鉴权相关配置属性】
 *
 * @author li.hongyu
 * @date 2026-03-03
 */
@Data
public class WebSocketAuthProperties {

    /**
     * 鉴权模式：
     * - GATEWAY_HEADERS：信任网关透传头（X-User-Id/X-User-Name）
     * - JWT：握手请求携带 JWT（Authorization 或 query param）
     * - NONE：不鉴权（不建议生产使用）
     */
    private WebSocketAuthMode mode = WebSocketAuthMode.GATEWAY_HEADERS;

    /**
     * JWT 模式下：Authorization 请求头名称
     */
    private String authorizationHeader = JwtHeaderConstants.AUTHORIZATION;

    /**
     * JWT 模式下：Authorization 前缀（默认 Bearer ）
     */
    private String authorizationPrefix = JwtHeaderConstants.BEARER_PREFIX;

    /**
     * JWT 模式下：query 参数 token 名称（当 Authorization 不存在时兜底）
     */
    private String tokenParam = "token";

    /**
     * JWT 模式下：期望 Token 类型（默认 ACCESS）
     */
    private TokenType expectedTokenType = TokenType.ACCESS;

    /**
     * 网关透传模式下：用户ID请求头
     */
    private String gatewayUserIdHeader = GatewayConstants.Headers.USER_ID;

    /**
     * 网关透传模式下：用户名请求头
     */
    private String gatewayUsernameHeader = GatewayConstants.Headers.USER_NAME;
}

