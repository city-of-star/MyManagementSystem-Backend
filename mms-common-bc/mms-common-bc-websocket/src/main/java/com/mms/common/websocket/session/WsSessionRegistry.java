package com.mms.common.websocket.session;

import org.springframework.web.socket.WebSocketSession;

import java.util.Set;

/**
 * 实现功能【WebSocket 会话注册表接口】
 * <p>
 * 维护 user/room 与连接之间的路由关系。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-03-26 16:28:47
 */
public interface WsSessionRegistry {

    void register(WebSocketSession session, WsSessionPrincipal principal);

    void unregister(WebSocketSession session);

    Set<WebSocketSession> getByUserId(String userId);

    Set<WebSocketSession> getByRoomId(String roomId);

    Set<WebSocketSession> getAllSessions();

    void joinRoom(String roomId, WebSocketSession session);

    void leaveRoom(String roomId, WebSocketSession session);
}

