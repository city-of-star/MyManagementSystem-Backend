package com.mms.common.websocket.service;

import com.mms.common.websocket.protocol.WsMessage;

/**
 * 实现功能【WebSocket 推送服务】
 * <p>
 * 提供按用户、按房间和全量广播能力。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-03-26 16:28:47
 */
public interface WsPushService {

    /**
     * 推送给指定用户的所有连接
     */
    void pushToUser(String userId, WsMessage<?> message);

    /**
     * 推送给指定房间内所有已 join 的连接
     */
    void pushToRoom(String roomId, WsMessage<?> message);

    /**
     * 广播给当前注册表中的全部会话
     */
    void broadcast(WsMessage<?> message);
}

