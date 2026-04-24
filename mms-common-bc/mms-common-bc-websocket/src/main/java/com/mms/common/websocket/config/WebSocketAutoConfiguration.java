package com.mms.common.websocket.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mms.common.core.utils.JacksonObjectMapperUtils;
import com.mms.common.security.servlet.service.GatewaySignatureVerificationService;
import com.mms.common.websocket.auth.GatewayCompatibleHandshakeHandler;
import com.mms.common.websocket.receive.dispatcher.WsReceiveTextDispatcher;
import com.mms.common.websocket.receive.handler.WsReceiverMessageHandler;
import com.mms.common.websocket.receive.handler.system.JoinRoomWsReceiverMessageHandler;
import com.mms.common.websocket.receive.handler.system.LeaveRoomWsReceiverMessageHandler;
import com.mms.common.websocket.receive.handler.system.PingWsReceiverMessageHandler;
import com.mms.common.websocket.auth.AuthHandshakeInterceptor;
import com.mms.common.websocket.common.properties.WebSocketProperties;
import com.mms.common.websocket.push.service.WsPushService;
import com.mms.common.websocket.push.service.impl.WsPushServiceImpl;
import com.mms.common.websocket.registry.service.impl.InMemoryWsRegistryServiceImpl;
import com.mms.common.websocket.registry.listener.WsRegistryListener;
import com.mms.common.websocket.registry.service.WsRegistryService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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
     * 会话注册服务
     */
    @Bean
    @ConditionalOnMissingBean
    public WsRegistryService wsRegistryService(WebSocketProperties properties, ObjectProvider<WsRegistryListener> listenersProvider) {
        return new InMemoryWsRegistryServiceImpl(properties, listenersProvider.orderedStream().toList());
    }

    /**
     * 握手拦截器
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
     * 握手处理器
     */
    @Bean
    @ConditionalOnMissingBean(HandshakeHandler.class)
    public HandshakeHandler gatewayCompatibleHandshakeHandler() {
        return new GatewayCompatibleHandshakeHandler();
    }

    @Bean
    @Order(1_000)
    public WsReceiverMessageHandler<?> pingWsMessageHandler(@Qualifier(WEBSOCKET_OBJECT_MAPPER_BEAN_NAME) ObjectMapper objectMapper) {
        return new PingWsReceiverMessageHandler(objectMapper);
    }

    @Bean
    @Order(1_000)
    public WsReceiverMessageHandler<?> joinRoomWsMessageHandler(WsRegistryService wsRegistryService) {
        return new JoinRoomWsReceiverMessageHandler(wsRegistryService);
    }

    @Bean
    @Order(1_000)
    public WsReceiverMessageHandler<?> leaveRoomWsMessageHandler(WsRegistryService wsRegistryService) {
        return new LeaveRoomWsReceiverMessageHandler(wsRegistryService);
    }

    /**
     * 文本处理器
     */
    @Bean
    @ConditionalOnMissingBean(WebSocketHandler.class)
    public WebSocketHandler wsReceiveTextDispatcher(WsRegistryService wsRegistryService, @Qualifier(WEBSOCKET_OBJECT_MAPPER_BEAN_NAME) ObjectMapper objectMapper, ObjectProvider<WsReceiverMessageHandler<?>> messageHandlers) {
        return new WsReceiveTextDispatcher(wsRegistryService, objectMapper, messageHandlers.orderedStream().toList());
    }

    /**
     * 推送服务
     */
    @Bean
    @ConditionalOnMissingBean
    public WsPushService wsPushService(WsRegistryService wsRegistryService, @Qualifier(WEBSOCKET_OBJECT_MAPPER_BEAN_NAME) ObjectMapper objectMapper) {
        return new WsPushServiceImpl(wsRegistryService, objectMapper);
    }

    /**
     * 注册 WebSocket 端点
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