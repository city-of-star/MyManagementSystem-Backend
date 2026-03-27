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

    void pushToUser(String userId, WsMessage<?> message);

    void pushToRoom(String roomId, WsMessage<?> message);

    void broadcast(WsMessage<?> message);
}

