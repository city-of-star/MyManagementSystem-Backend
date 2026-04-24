package com.mms.usercenter.common.security.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 实现功能【在线用户移除事件】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-04-24 14:04:53
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnlineUserRemoveEvent {

    /**
     * 用户ID
     */
    private String userId;
}