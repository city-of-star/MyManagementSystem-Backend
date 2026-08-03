package com.mms.usercenter.service.message.service;

import com.mms.usercenter.common.message.vo.MsgUnreadVo;

/**
 * 实现功能【消息未读服务】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
public interface MsgUnreadService {

    MsgUnreadVo getCurrentUnread();
}
