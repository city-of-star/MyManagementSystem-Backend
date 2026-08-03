package com.mms.usercenter.service.message.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.usercenter.common.message.dto.MsgSysInboxPageQueryDto;
import com.mms.usercenter.common.message.dto.MsgSysInboxStarDto;
import com.mms.usercenter.common.message.vo.MsgSysInboxVo;

/**
 * 实现功能【系统收件箱服务】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
public interface MsgSysInboxService {

    Page<MsgSysInboxVo> getInboxPage(MsgSysInboxPageQueryDto dto);

    MsgSysInboxVo getInboxById(Long id);

    void markRead(Long id);

    void markAllRead();

    void star(MsgSysInboxStarDto dto);

    void deleteInbox(Long id);
}
