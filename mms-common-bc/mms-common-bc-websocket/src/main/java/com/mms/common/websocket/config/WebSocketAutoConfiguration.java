package com.mms.common.websocket.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mms.common.websocket.handler.DefaultTextWebSocketHandler;
import com.mms.common.websocket.interceptor.AuthHandshakeInterceptor;
import com.mms.common.websocket.properties.WebSocketProperties;
import com.mms.common.websocket.service.WsPushService;
import com.mms.common.websocket.service.impl.WsPushServiceImpl;
import com.mms.common.websocket.session.InMemoryWsSessionRegistry;
import com.mms.common.websocket.session.WsSessionRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;

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

    @Bean
    @ConditionalOnMissingBean
    public WsSessionRegistry wsSessionRegistry() {
        return new InMemoryWsSessionRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthHandshakeInterceptor authHandshakeInterceptor(WebSocketProperties properties) {
        return new AuthHandshakeInterceptor(properties);
    }

    @Bean
    @ConditionalOnMissingBean(WebSocketHandler.class)
    public WebSocketHandler defaultTextWebSocketHandler(WsSessionRegistry wsSessionRegistry, ObjectMapper objectMapper) {
        return new DefaultTextWebSocketHandler(wsSessionRegistry, objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public WsPushService wsPushService(WsSessionRegistry wsSessionRegistry, ObjectMapper objectMapper) {
        return new WsPushServiceImpl(wsSessionRegistry, objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public WebSocketConfigurer webSocketConfigurer(WebSocketProperties properties, WebSocketHandler webSocketHandler, AuthHandshakeInterceptor authHandshakeInterceptor) {
        return registry -> registry.addHandler(webSocketHandler, properties.getEndpoint())
                .addInterceptors(authHandshakeInterceptor)
                .setAllowedOrigins("*");
    }
}