package com.mms.usercenter.service.security.service;

import com.mms.usercenter.common.security.vo.OnlineUserVo;

import java.util.List;

/**
 * 实现功能【在线用户服务】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-04-03 14:44:03
 */
public interface OnlineUserService {

    /**
     * WebSocket 会话注册完成后触发
     */
    void onSessionRegistered();

    /**
     * WebSocket 会话注销完成后触发
     */
    void onSessionUnregistered();

    /**
     * 用户订阅在线用户房间后触发
     */
    void onOnlineUserRoomJoined();

    /**
     * 查询在线用户列表
     */
    List<OnlineUserVo> getOnlineUsers();

    /**
     * 强制指定用户下线
     */
    void forceLogout(Long userId);
}