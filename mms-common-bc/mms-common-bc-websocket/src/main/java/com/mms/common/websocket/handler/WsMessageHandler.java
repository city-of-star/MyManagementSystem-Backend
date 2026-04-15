package com.mms.common.websocket.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.mms.common.websocket.protocol.WsMessage;
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
public interface WsMessageHandler {

    /**
     * 自定义消息类型
     */
    String supportType();

    /**
     * 处理一条已反序列化的消息
     */
    void handle(WebSocketSession session, WsMessage<JsonNode> message) throws Exception;
}

