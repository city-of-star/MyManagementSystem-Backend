package com.mms.common.websocket.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 实现功能【WebSocket 消息协议对象】
 * <p>
 * 统一前后端消息结构：type/requestId/data/timestamp。
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

    private String type;
    private String requestId;
    private T data;
    private Long timestamp;
}

