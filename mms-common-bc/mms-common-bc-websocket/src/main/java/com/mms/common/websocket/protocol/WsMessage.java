package com.mms.common.websocket.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 实现功能【WebSocket 消息协议对象】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-03-26 16:28:47
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WsMessage<T> {

    /**
     * 消息类型
     */
    private String type;
    /**
     * 业务负载
     */
    private T data;
    /**
     * 请求-响应关联
     */
    private String requestId;
    /**
     * 毫秒时间戳
     */
    private Long timestamp;
}

