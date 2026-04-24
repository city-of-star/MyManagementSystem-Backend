package com.mms.usercenter.common.security.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 实现功能【在线用户新增/更新事件】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-04-24 14:04:44
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnlineUserUpsertEvent {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 当前在线会话数
     */
    private Integer sessionCount;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 最近登录 IP
     */
    private String loginIp;

    /**
     * 最近登录时间（格式化文本）
     */
    private String loginTime;

    /**
     * 最近活跃时间（格式化文本）
     */
    private String lastActiveTime;
}