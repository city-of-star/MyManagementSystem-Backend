package com.mms.common.websocket.properties;

import com.mms.common.core.constants.gateway.GatewayConstants;
import com.mms.common.websocket.constants.WebSocketConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 实现功能【WebSocket 模块配置属性】
 * <p>
 * 对外暴露 websocket 模块常见配置，便于业务服务按需覆盖。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-03-26 16:28:47
 */
@Data
@ConfigurationProperties(prefix = "mms.websocket")
public class WebSocketProperties {

    /**
     * WebSocket 端点路径
     */
    private String endpoint = "/ws";

    /**
     * 握手阶段读取用户ID的请求头
     */
    private String userIdHeader = GatewayConstants.Headers.USER_ID;

    /**
     * 是否开启鉴权
     */
    private boolean authEnabled = true;

    /**
     * 单条文本消息最大大小（字节），默认 64KB
     */
    private int textMessageSizeLimit = 64 * 1024;

    /**
     * 单个会话的发送超时时间（毫秒），默认 10s
     */
    private int sendTimeLimitMs = 10_000;

    /**
     * 单个会话的发送缓冲区大小（字节），默认 512KB
     */
    private int sendBufferSizeBytes = 512 * 1024;
}

