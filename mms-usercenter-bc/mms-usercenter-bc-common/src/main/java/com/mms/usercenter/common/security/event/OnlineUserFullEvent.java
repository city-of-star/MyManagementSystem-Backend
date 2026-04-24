package com.mms.usercenter.common.security.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 实现功能【在线用户全量事件】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-04-24 14:04:31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnlineUserFullEvent {

    /**
     * 全量在线用户列表
     */
    private List<OnlineUserUpsertEvent> users;
}