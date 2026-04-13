package com.mms.usercenter.service.websocket.listener;

import com.mms.common.websocket.service.WsRegistryListener;
import com.mms.usercenter.service.security.service.OnlineUserService;
import lombok.AllArgsConstructor;
import org.springframework.core.annotation.Order;

/**
 * 在线用户 WebSocket 订阅监听器。
 * <p>
 * 通过监听 WebSocket Registry 的生命周期事件驱动在线用户推送，
 * 避免通过继承默认 Registry 实现来插入业务逻辑。
 * </p>
 */
@Order
@AllArgsConstructor
public class OnlineUserWsRegistryListener implements WsRegistryListener {

    private static final String ROOM_ONLINE_USER = "security_online_user";

    private final OnlineUserService onlineUserService;

    @Override
    public void onRegistered(org.springframework.web.socket.WebSocketSession session, com.mms.common.websocket.session.WsSessionPrincipal principal) {
        onlineUserService.onSessionRegistered();
    }

    @Override
    public void onUnregistered(String sessionId, String userId) {
        onlineUserService.onSessionUnregistered();
    }

    @Override
    public void onRoomJoined(String roomId, String sessionId, String userId) {
        if (!ROOM_ONLINE_USER.equals(roomId)) {
            return;
        }
        onlineUserService.onOnlineUserRoomJoined();
    }
}

