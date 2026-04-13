package com.mms.usercenter.service.websocket.config;

import com.mms.usercenter.service.security.service.OnlineUserService;
import com.mms.common.websocket.service.WsRegistryListener;
import com.mms.usercenter.service.websocket.listener.OnlineUserWsRegistryListener;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 实现功能【UserCenter WebSocket 配置】
 */
@Configuration
public class UsercenterWebSocketConfiguration {

    @Bean
    public WsRegistryListener onlineUserWsRegistryListener(ObjectProvider<OnlineUserService> onlineUserServiceProvider) {
        return new OnlineUserWsRegistryListener(onlineUserServiceProvider);
    }
}

