package com.mms.common.websocket.handler;

import org.springframework.lang.Nullable;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.util.List;

/**
 * 实现功能【与 Spring Cloud Gateway（Netty WebSocket 客户端转发）兼容的握手处理器】
 * <p>
 * {@link DefaultHandshakeHandler} 继承的 {@link org.springframework.web.socket.server.support.AbstractHandshakeHandler}
 * 通过 {@code selectProtocol} 在「客户端请求的 Sec-WebSocket-Protocol」与「Handler 支持的子协议」之间求交
 * 本项目的 {@link org.springframework.web.socket.handler.TextWebSocketHandler} 未声明子协议，默认结果为 null
 * 响应中不带 {@code Sec-WebSocket-Protocol}。网关在转发到下游后校验握手响应时会得到 {@code Actual: null}
 * 抛出 {@code WebSocketClientHandshakeException: Invalid subprotocol}
 * 浏览器通过 {@code new WebSocket(url, ['bearer', token])} 会请求两个子协议。此处优先协商 {@code bearer}
 * 否则回退为客户端列表中的第一项，使响应头与网关/Netty 客户端期望一致
 * </p>
 *
 * @author li.hongyu
 * @date 2026-03-27 10:00:00
 */
public class GatewayCompatibleHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    @Nullable
    protected String selectProtocol(List<String> requestedProtocols, WebSocketHandler wsHandler) {
        if (requestedProtocols == null || requestedProtocols.isEmpty()) {
            return super.selectProtocol(requestedProtocols, wsHandler);
        }
        for (String protocol : requestedProtocols) {
            if (protocol != null && "bearer".equalsIgnoreCase(protocol.trim())) {
                return protocol.trim();
            }
        }
        return requestedProtocols.get(0);
    }
}
