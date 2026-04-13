package com.mms.common.websocket.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mms.common.core.utils.JacksonObjectMapperUtils;
import com.mms.common.security.servlet.service.GatewaySignatureVerificationService;
import com.mms.common.websocket.handler.DefaultTextWebSocketHandler;
import com.mms.common.websocket.handler.GatewayCompatibleHandshakeHandler;
import com.mms.common.websocket.interceptor.AuthHandshakeInterceptor;
import com.mms.common.websocket.properties.WebSocketProperties;
import com.mms.common.websocket.service.WsPushService;
import com.mms.common.websocket.service.impl.WsPushServiceImpl;
import com.mms.common.websocket.service.impl.InMemoryWsRegistryServiceImpl;
import com.mms.common.websocket.protocol.WsMessage;
import com.mms.common.websocket.service.WsRegistryService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.server.HandshakeHandler;

/**
 * 实现功能【WebSocket 模块自动装配】
 * <p>
 * 提供默认的会话注册表、握手拦截器、文本处理器和推送服务，并完成端点注册。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-03-27 10:00:00
 */
@Configuration
@EnableWebSocket
@ConditionalOnClass(WebSocketConfigurer.class)
@EnableConfigurationProperties(WebSocketProperties.class)
public class WebSocketAutoConfiguration {

    /**
     * WebSocket 文本帧 JSON 序列化/反序列化
     */
    public static final String WEBSOCKET_OBJECT_MAPPER_BEAN_NAME = "websocketObjectMapper";

    /**
     * 会话注册表：默认使用内存实现；业务可自定义 Bean 覆盖为 Redis 等分布式实现
     */
    @Bean
    @ConditionalOnMissingBean
    public WsRegistryService wsSessionRegistry() {
        return new InMemoryWsRegistryServiceImpl();
    }

    /**
     * 握手拦截器：从 HTTP 请求头解析用户标识并写入会话 attributes
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthHandshakeInterceptor authHandshakeInterceptor(WebSocketProperties properties, ObjectProvider<GatewaySignatureVerificationService> gatewaySignatureVerificationServiceProvider) {
        GatewaySignatureVerificationService verificationService = gatewaySignatureVerificationServiceProvider.getIfAvailable();
        if (properties.isAuthEnabled() && verificationService == null) {
            throw new IllegalStateException("WebSocket 鉴权已开启，但缺少网关签名验证组件 GatewaySignatureVerificationService，无法保证握手请求来自网关，已阻止服务启动。请配置 `gateway.signature.public-key` 以启用 WebSocket 握手阶段的网关签名验签。");
        }
        return new AuthHandshakeInterceptor(properties, verificationService);
    }

    /**
     * WebSocket 专用 JSON 序列化器
     */
    @Bean(name = WEBSOCKET_OBJECT_MAPPER_BEAN_NAME)
    @ConditionalOnMissingBean(name = WEBSOCKET_OBJECT_MAPPER_BEAN_NAME)
    public ObjectMapper websocketObjectMapper() {
        return JacksonObjectMapperUtils.createCommonObjectMapper();
    }

    /**
     * 握手处理器：回显 {@code Sec-WebSocket-Protocol}，避免经网关转发时 Netty 客户端子协议校验失败
     */
    @Bean
    @ConditionalOnMissingBean(HandshakeHandler.class)
    public HandshakeHandler gatewayCompatibleHandshakeHandler() {
        return new GatewayCompatibleHandshakeHandler();
    }

    /**
     * 默认文本处理器：连接建立时注册、处理 ping/进房/退房、断开时清理
     */
    @Bean
    @ConditionalOnMissingBean(WebSocketHandler.class)
    public WebSocketHandler defaultTextWebSocketHandler(WsRegistryService wsRegistryService, @Qualifier(WEBSOCKET_OBJECT_MAPPER_BEAN_NAME) ObjectMapper objectMapper) {
        return new DefaultTextWebSocketHandler(wsRegistryService, objectMapper);
    }

    /**
     * 推送服务：将 {@link WsMessage} 序列化为 JSON 文本并推送到对应会话
     */
    @Bean
    @ConditionalOnMissingBean
    public WsPushService wsPushService(WsRegistryService wsRegistryService, @Qualifier(WEBSOCKET_OBJECT_MAPPER_BEAN_NAME) ObjectMapper objectMapper) {
        return new WsPushServiceImpl(wsRegistryService, objectMapper);
    }

    /**
     * 注册 WebSocket 端点：路径来自配置 {@link WebSocketProperties#getEndpoint()}，并挂载鉴权拦截器
     */
    @Bean
    @ConditionalOnMissingBean
    public WebSocketConfigurer webSocketConfigurer(WebSocketProperties properties, WebSocketHandler webSocketHandler, AuthHandshakeInterceptor authHandshakeInterceptor, HandshakeHandler handshakeHandler) {
        return registry -> registry.addHandler(webSocketHandler, properties.getEndpoint())
                .addInterceptors(authHandshakeInterceptor)
                .setHandshakeHandler(handshakeHandler)
                .setAllowedOrigins("*");
    }
}