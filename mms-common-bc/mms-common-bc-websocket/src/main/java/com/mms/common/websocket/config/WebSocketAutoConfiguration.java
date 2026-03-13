package com.mms.common.websocket.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mms.common.security.core.utils.JwtUtils;
import com.mms.common.security.core.utils.TokenValidatorUtils;
import com.mms.common.websocket.auth.PrincipalHandshakeHandler;
import com.mms.common.websocket.auth.WebSocketHandshakeInterceptor;
import com.mms.common.websocket.handler.DefaultTextWebSocketHandler;
import com.mms.common.websocket.properties.WebSocketProperties;
import com.mms.common.websocket.session.WebSocketSender;
import com.mms.common.websocket.session.WebSocketSessionRegistry;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * 实现功能【WebSocket 通用封装自动装配配置】
 *
 * @author li.hongyu
 * @date 2026-03-03
 */
@Slf4j
@Configuration
@ConditionalOnClass({WebSocketConfigurer.class, HttpServletRequest.class})
@EnableConfigurationProperties(WebSocketProperties.class)
@ConditionalOnProperty(prefix = "websocket", name = "enabled", havingValue = "true", matchIfMissing = true)
public class WebSocketAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public WebSocketSessionRegistry mmsWebSocketSessionRegistry(WebSocketProperties properties) {
        return new WebSocketSessionRegistry(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public WebSocketSender mmsWebSocketSender(
            WebSocketSessionRegistry registry,
            ObjectProvider<ObjectMapper> objectMapperProvider) {
        return new WebSocketSender(registry, objectMapperProvider.getIfAvailable());
    }

    @Bean
    @ConditionalOnMissingBean
    public WebSocketHandshakeInterceptor mmsWebSocketHandshakeInterceptor(
            WebSocketProperties properties,
            ObjectProvider<JwtUtils> jwtUtilsProvider,
            ObjectProvider<TokenValidatorUtils> tokenValidatorUtilsProvider) {
        return new WebSocketHandshakeInterceptor(
                properties,
                jwtUtilsProvider.getIfAvailable(),
                tokenValidatorUtilsProvider.getIfAvailable()
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public PrincipalHandshakeHandler mmsPrincipalHandshakeHandler(WebSocketProperties properties) {
        return new PrincipalHandshakeHandler(properties);
    }

    /**
     * 默认 WebSocketHandler（业务侧可提供同名 Bean：mmsWebSocketHandler 覆盖）
     */
    @Bean(name = "mmsWebSocketHandler")
    @ConditionalOnMissingBean(name = "mmsWebSocketHandler")
    public WebSocketHandler mmsWebSocketHandler(
            WebSocketSessionRegistry registry,
            WebSocketSender sender,
            ObjectProvider<ObjectMapper> objectMapperProvider) {
        return new DefaultTextWebSocketHandler(registry, sender, objectMapperProvider.getIfAvailable());
    }

    /**
     * 自动注册 endpoint：
     * - 默认 path：/ws
     * - 默认允许跨域：*
     */
    @Bean
    @ConditionalOnMissingBean(WebSocketConfigurer.class)
    @ConditionalOnProperty(prefix = "websocket", name = "auto-register", havingValue = "true", matchIfMissing = true)
    public WebSocketConfigurer mmsWebSocketConfigurer(
            WebSocketProperties properties,
            WebSocketHandshakeInterceptor handshakeInterceptor,
            PrincipalHandshakeHandler handshakeHandler,
            WebSocketHandler mmsWebSocketHandler) {
        return new WebSocketConfigurer() {
            @Override
            public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
                String path = properties.getEndpointPath();
                String[] allowed = properties.getAllowedOrigins() == null
                        ? new String[0]
                        : properties.getAllowedOrigins().toArray(new String[0]);

                var registration = registry.addHandler(mmsWebSocketHandler, path)
                        .addInterceptors(handshakeInterceptor)
                        .setAllowedOrigins(allowed)
                        .setHandshakeHandler(handshakeHandler);

                if (properties.isSockJsEnabled()) {
                    registration.withSockJS();
                }

                log.info("初始化WebSocket endpoint：path={}, authMode={}, sockJs={}",
                        path, properties.getAuth().getMode(), properties.isSockJsEnabled());
            }
        };
    }
}

