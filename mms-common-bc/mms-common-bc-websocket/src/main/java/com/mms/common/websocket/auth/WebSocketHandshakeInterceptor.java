package com.mms.common.websocket.auth;

import com.mms.common.core.enums.jwt.TokenType;
import com.mms.common.security.core.constants.JwtClaimsConstants;
import com.mms.common.security.core.utils.JwtUtils;
import com.mms.common.security.core.utils.TokenValidatorUtils;
import com.mms.common.websocket.properties.WebSocketProperties;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * 实现功能【WebSocket 握手鉴权拦截器】
 * <p>
 * 支持两种常见企业架构：
 * - 网关已做 JWT 验证：下游服务信任网关透传的 X-User-Id/X-User-Name
 * - 直连服务：握手阶段携带 JWT（Authorization 或 query param）
 *
 * @author li.hongyu
 * @date 2026-03-03
 */
@Slf4j
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    private final WebSocketProperties properties;
    private final JwtUtils jwtUtils;
    private final TokenValidatorUtils tokenValidatorUtils;

    public WebSocketHandshakeInterceptor(
            WebSocketProperties properties,
            JwtUtils jwtUtils,
            TokenValidatorUtils tokenValidatorUtils) {
        this.properties = properties;
        this.jwtUtils = jwtUtils;
        this.tokenValidatorUtils = tokenValidatorUtils;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        switch (properties.getAuth().getMode()) {
            case NONE -> {
                return true;
            }
            case GATEWAY_HEADERS -> {
                return authByGatewayHeaders(request, response, attributes);
            }
            case JWT -> {
                return authByJwt(request, response, attributes);
            }
            default -> {
                reject(response, "unsupported auth mode");
                return false;
            }
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // no-op
    }

    private boolean authByGatewayHeaders(ServerHttpRequest request, ServerHttpResponse response, Map<String, Object> attributes) {
        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            reject(response, "not servlet request");
            return false;
        }

        String userIdHeader = properties.getAuth().getGatewayUserIdHeader();
        String usernameHeader = properties.getAuth().getGatewayUsernameHeader();

        String userId = servletRequest.getServletRequest().getHeader(userIdHeader);
        String username = servletRequest.getServletRequest().getHeader(usernameHeader);

        if (!StringUtils.hasText(userId) && !StringUtils.hasText(username)) {
            log.warn("WebSocket握手拒绝：网关透传头缺失，{}={}, {}={}", userIdHeader, userId, usernameHeader, username);
            reject(response, "missing gateway headers");
            return false;
        }

        putIdentity(attributes, userId, username);
        return true;
    }

    private boolean authByJwt(ServerHttpRequest request, ServerHttpResponse response, Map<String, Object> attributes) {
        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            reject(response, "not servlet request");
            return false;
        }

        if (jwtUtils == null && tokenValidatorUtils == null) {
            log.warn("WebSocket握手拒绝：JWT模式但未发现 JwtUtils/TokenValidatorUtils Bean（请配置 jwt.secret 或引入 security 自动装配）");
            reject(response, "jwt bean missing");
            return false;
        }

        String token = extractToken(servletRequest);
        if (!StringUtils.hasText(token)) {
            reject(response, "token missing");
            return false;
        }

        try {
            Claims claims = parseAndValidate(token);
            String userId = claims.get(JwtClaimsConstants.USER_ID) != null ? claims.get(JwtClaimsConstants.USER_ID).toString() : null;
            String username = claims.get(JwtClaimsConstants.USERNAME) != null ? claims.get(JwtClaimsConstants.USERNAME).toString() : null;

            if (!StringUtils.hasText(userId) && !StringUtils.hasText(username)) {
                reject(response, "token missing identity");
                return false;
            }

            putIdentity(attributes, userId, username);
            return true;
        } catch (Exception e) {
            log.warn("WebSocket握手拒绝：JWT解析/验证失败：{}", e.getMessage());
            reject(response, "invalid token");
            return false;
        }
    }

    private Claims parseAndValidate(String token) {
        if (tokenValidatorUtils != null) {
            TokenType expected = properties.getAuth().getExpectedTokenType();
            return tokenValidatorUtils.parseAndValidate(token, expected);
        }
        return jwtUtils.parseToken(token);
    }

    private String extractToken(ServletServerHttpRequest servletRequest) {
        String headerName = properties.getAuth().getAuthorizationHeader();
        String prefix = properties.getAuth().getAuthorizationPrefix();
        String authHeader = servletRequest.getServletRequest().getHeader(headerName);

        if (StringUtils.hasText(authHeader) && StringUtils.hasText(prefix) && authHeader.startsWith(prefix)) {
            String token = authHeader.substring(prefix.length()).trim();
            if (StringUtils.hasText(token)) {
                return token;
            }
        }

        String tokenParam = properties.getAuth().getTokenParam();
        return servletRequest.getServletRequest().getParameter(tokenParam);
    }

    private void putIdentity(Map<String, Object> attributes, String userId, String username) {
        String userIdKey = properties.getSession().getUserIdKey();
        String usernameKey = properties.getSession().getUsernameKey();

        if (StringUtils.hasText(userId)) {
            attributes.put(userIdKey, userId);
        }
        if (StringUtils.hasText(username)) {
            attributes.put(usernameKey, username);
        }
    }

    private void reject(ServerHttpResponse response, String reason) {
        if (response instanceof ServletServerHttpResponse servletResponse) {
            servletResponse.getServletResponse().setStatus(HttpStatus.UNAUTHORIZED.value());
        }
        log.debug("WebSocket握手拒绝：reason={}", reason);
    }
}

