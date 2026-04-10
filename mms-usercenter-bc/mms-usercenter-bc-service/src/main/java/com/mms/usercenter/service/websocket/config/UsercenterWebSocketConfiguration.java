package com.mms.usercenter.service.websocket.config;

import com.mms.common.websocket.service.WsRegistryService;
import com.mms.usercenter.service.security.service.OnlineUserService;
import com.mms.usercenter.service.websocket.registry.UsercenterWsRegistryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 实现功能【UserCenter WebSocket 配置】
 */
@Configuration
public class UsercenterWebSocketConfiguration {

    @Bean
    public WsRegistryService usercenterWsRegistryService(OnlineUserService onlineUserService) {
        return new UsercenterWsRegistryService(onlineUserService);
    }
}

