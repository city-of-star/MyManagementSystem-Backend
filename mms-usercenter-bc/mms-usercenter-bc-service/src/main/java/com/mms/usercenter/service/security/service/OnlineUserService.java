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
     * 查询在线用户列表
     */
    List<OnlineUserVo> getOnlineUsers();

    /**
     * 强制指定用户下线
     */
    void forceLogout(Long userId);
}