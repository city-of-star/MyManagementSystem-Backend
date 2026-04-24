package com.mms.common.websocket.receive.router;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mms.common.websocket.common.protocol.WsMessage;
import com.mms.common.websocket.receive.handler.WsReceiverMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 实现功能【WebSocket 接收消息路由器】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-04-24 09:48:50
 */
@Slf4j
public class WsReceiveRouter {

    private final ObjectMapper objectMapper;
    /**
     * 消息类型与处理器映射表（type → handler）
     */
    private final Map<String, WsReceiverMessageHandler<?>> handlersByType;

    public WsReceiveRouter(ObjectMapper objectMapper, List<WsReceiverMessageHandler<?>> handlers) {
        this.objectMapper = objectMapper;
        this.handlersByType = buildHandlerMap(handlers);
    }

    /**
     * 构建消息类型与处理器映射表
     */
    private static Map<String, WsReceiverMessageHandler<?>> buildHandlerMap(List<WsReceiverMessageHandler<?>> handlers) {
        if (handlers == null || handlers.isEmpty()) {
            return Map.of();
        }
        Map<String, WsReceiverMessageHandler<?>> map = new LinkedHashMap<>();
        for (WsReceiverMessageHandler<?> h : handlers) {
            if (h == null) {
                continue;
            }
            String type = h.getMessageType();
            if (type == null || type.isBlank()) {
                log.warn("跳过消息类型为空的 WsReceiverMessageHandler: {}", h.getClass().getName());
                continue;
            }
            WsReceiverMessageHandler<?> existing = map.putIfAbsent(type, h);
            if (existing != null) {
                log.warn("重复的 WsReceiverMessageHandler: type={}, 保留 {}, 忽略 {}", type, existing.getClass().getName(), h.getClass().getName());
            }
        }
        return Map.copyOf(map);
    }

    /**
     * 分发消息
     */
    public void dispatch(WebSocketSession session, WsMessage<JsonNode> rawMessage) throws Exception {
        // 获取消息类型
        String type = rawMessage != null && rawMessage.getType() != null ? rawMessage.getType() : "";
        // 根据消息类型获取消息处理器
        WsReceiverMessageHandler<?> handler = handlersByType.get(type);
        // 如果处理器为空，则记录日志
        if (handler == null) {
            log.debug("未定义的消息类型，type={}, sessionId={}", type, session != null ? session.getId() : null);
            return;
        }
        dispatch0(session, rawMessage, handler);
    }

    /**
     * 分发消息（内部实现）
     */
    @SuppressWarnings("unchecked")
    private <T> void dispatch0(WebSocketSession session, WsMessage<JsonNode> rawMessage, WsReceiverMessageHandler<?> handler0) throws Exception {
        // 转换为泛型处理器
        WsReceiverMessageHandler<T> handler = (WsReceiverMessageHandler<T>) handler0;
        // 获取DTO类型
        Class<T> dtoClass = handler.getDtoClass();
        // 如果消息负载不为空，则转换为DTO对象
        T dto = null;
        if (rawMessage != null && rawMessage.getData() != null) {
            if (dtoClass == null) {
                throw new IllegalStateException("消息负载类型不能为空, handler=" + handler.getClass().getName());
            }
            dto = objectMapper.treeToValue(rawMessage.getData(), dtoClass);
        }
        // 构建消息对象
        WsMessage<T> typedMessage = WsMessage.<T>builder()
                .type(rawMessage != null ? rawMessage.getType() : null)
                .data(dto)
                .requestId(rawMessage != null ? rawMessage.getRequestId() : null)
                .timestamp(rawMessage != null ? rawMessage.getTimestamp() : null)
                .build();
        // 处理消息
        handler.handle(session, typedMessage);
    }
}

