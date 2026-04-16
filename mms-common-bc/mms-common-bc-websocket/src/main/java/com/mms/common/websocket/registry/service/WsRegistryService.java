package com.mms.common.websocket.registry.service;

import com.mms.common.websocket.common.session.WsSessionPrincipal;
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
public interface WsRegistryService {

    /**
     * 连接建立后注册会话
     */
    void register(WebSocketSession session, WsSessionPrincipal principal);

    /**
     * 连接关闭或异常时移除会话
     */
    void unregister(WebSocketSession session);

    /**
     * 按用户 ID 查询当前所有活跃连接
     */
    Set<WebSocketSession> getByUserId(String userId);

    /**
     * 按房间 ID 查询该房间内所有连接
     */
    Set<WebSocketSession> getByRoomId(String roomId);

    /**
     * 返回当前已注册的全部会话
     */
    Set<WebSocketSession> getAllSessions();

    /**
     * 将会话加入指定房间
     */
    void joinRoom(String roomId, WebSocketSession session);

    /**
     * 将会话从指定房间移除
     */
    void leaveRoom(String roomId, WebSocketSession session);
}

