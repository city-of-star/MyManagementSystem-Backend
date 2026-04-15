package com.mms.usercenter.service.websocket.listener;

import com.mms.common.websocket.service.WsRegistryListener;
import com.mms.common.websocket.session.WsSessionPrincipal;
import com.mms.usercenter.service.security.service.OnlineUserService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.annotation.Order;
import org.springframework.web.socket.WebSocketSession;

/**
 * 在线用户 WebSocket 订阅监听器。
 * <p>
 * 通过监听 WebSocket Registry 的生命周期事件驱动在线用户推送，
 * 避免通过继承默认 Registry 实现来插入业务逻辑。
 * </p>
 */
@Order
public class OnlineUserWsRegistryListener implements WsRegistryListener {

    private static final String ROOM_ONLINE_USER = "security_online_user";

    private final ObjectProvider<OnlineUserService> onlineUserServiceProvider;

    public OnlineUserWsRegistryListener(ObjectProvider<OnlineUserService> onlineUserServiceProvider) {
        this.onlineUserServiceProvider = onlineUserServiceProvider;
    }

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
        if (!ROOM_ONLINE_USER.equals(roomId)) {
            return;
        }
        OnlineUserService svc = onlineUserServiceProvider.getIfAvailable();
        if (svc != null) {
            svc.onOnlineUserRoomJoined();
        }
    }
}

