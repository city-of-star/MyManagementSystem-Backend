package com.mms.common.websocket.receive.handler;

import com.mms.common.websocket.common.protocol.WsMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * 实现功能【WebSocket 文本消息按 type 分发处理器】
 * <p>
 * 业务模块可实现本接口并注册为 Spring Bean，扩展自定义消息类型
 * </p>
 *
 * @author li.hongyu
 * @date 2026-04-15 10:15:26
 */
public interface WsReceiverMessageHandler<T> {

    /**
     * 自定义消息类型
     */
    String getMessageType();

    /**
     * 当前消息类型对应的业务负载 DTO 类型
     */
    Class<T> getDtoClass();

    /**
     * 处理一条已反序列化的消息
     */
    void handle(WebSocketSession session, WsMessage<T> message) throws Exception;
}

