package com.mms.usercenter.service.websocket.registry;

import com.mms.common.websocket.service.impl.InMemoryWsRegistryServiceImpl;
import com.mms.common.websocket.session.WsSessionPrincipal;
import com.mms.usercenter.service.security.service.OnlineUserService;
import org.springframework.web.socket.WebSocketSession;

/**
 * 实现功能【UserCenter WebSocket 注册表】
 * <p>
 * 负责会话注册索引，在线用户推送逻辑委派给 OnlineUserService。
 * </p>
 */
public class UsercenterWsRegistryService extends InMemoryWsRegistryServiceImpl {

    private static final String ROOM_ONLINE_USER = "security_online_user";
    private final OnlineUserService onlineUserService;

    public UsercenterWsRegistryService(OnlineUserService onlineUserService) {
        this.onlineUserService = onlineUserService;
    }

    @Override
    public synchronized void register(WebSocketSession session, WsSessionPrincipal principal) {
        super.register(session, principal);
        onlineUserService.onSessionRegistered();
    }

    @Override
    public synchronized void unregister(WebSocketSession session) {
        super.unregister(session);
        onlineUserService.onSessionUnregistered();
    }

    @Override
    public synchronized void joinRoom(String roomId, WebSocketSession session) {
        super.joinRoom(roomId, session);
        if (!ROOM_ONLINE_USER.equals(roomId)) {
            return;
        }
        onlineUserService.onOnlineUserRoomJoined();
    }
}

