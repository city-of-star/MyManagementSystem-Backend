package com.mms.usercenter.service.websocket.listener;

import com.mms.common.websocket.registry.listener.WsRegistryListener;
import com.mms.common.websocket.common.session.WsSessionPrincipal;
import com.mms.usercenter.common.security.constants.OnlineUserConstants;
import com.mms.usercenter.service.security.service.OnlineUserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.annotation.Order;
import org.springframework.web.socket.WebSocketSession;

/**
 * 实现功能【在线用户 WebSocket 订阅监听器】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-04-03 14:45:41
 */
@Order
@AllArgsConstructor
public class OnlineUserWsRegistryListener implements WsRegistryListener {

    private final ObjectProvider<OnlineUserService> onlineUserServiceProvider;

    @Override
    public void onRegistered(WebSocketSession session, WsSessionPrincipal principal) {
        OnlineUserService svc = onlineUserServiceProvider.getIfAvailable();
        if (svc != null) {
            svc.onSessionRegistered();
        }
    }

    @Override
    public void onUnregistered(String sessionId, String userId) {
        OnlineUserService svc = onlineUserServiceProvider.getIfAvailable();
        if (svc != null) {
            svc.onSessionUnregistered();
        }
    }

    @Override
    public void onRoomJoined(String roomId, String sessionId, String userId) {
        if (!OnlineUserConstants.ROOM_ONLINE_USER.equals(roomId)) {
            return;
        }
        OnlineUserService svc = onlineUserServiceProvider.getIfAvailable();
        if (svc != null) {
            svc.onOnlineUserRoomJoined();
        }
    }
}

