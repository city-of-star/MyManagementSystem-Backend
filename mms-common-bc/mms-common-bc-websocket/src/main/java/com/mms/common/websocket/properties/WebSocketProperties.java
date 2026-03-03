package com.mms.common.websocket.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 实现功能【WebSocket 配置属性】
 *
 * @author li.hongyu
 * @date 2026-03-03
 */
@Data
@ConfigurationProperties(prefix = "websocket")
public class WebSocketProperties {

    /**
     * 是否启用 WebSocket 通用封装
     */
    private boolean enabled = true;

    /**
     * 是否自动注册默认 endpoint（业务侧不想自动注册时可关闭，只复用工具类）
     */
    private boolean autoRegister = true;

    /**
     * WebSocket Endpoint 路径（例如：/ws）
     */
    private String endpointPath = "/ws";

    /**
     * 允许的跨域来源（按 Spring WebSocket 规则）
     */
    private List<String> allowedOrigins = List.of("*");

    /**
     * 是否启用 SockJS（需要前端配套；默认关闭）
     */
    private boolean sockJsEnabled = false;

    /**
     * 鉴权相关配置
     */
    private WebSocketAuthProperties auth = new WebSocketAuthProperties();

    /**
     * Session相关配置
     */
    private WebSocketSessionProperties session = new WebSocketSessionProperties();
}

