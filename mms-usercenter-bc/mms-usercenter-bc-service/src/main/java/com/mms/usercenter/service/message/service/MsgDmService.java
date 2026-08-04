package com.mms.usercenter.service.message.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.usercenter.common.message.dto.MsgDmConversationPageQueryDto;
import com.mms.usercenter.common.message.dto.MsgDmMessagePageQueryDto;
import com.mms.usercenter.common.message.dto.MsgDmOpenDto;
import com.mms.usercenter.common.message.dto.MsgDmSendDto;
import com.mms.usercenter.common.message.vo.MsgDmConversationVo;
import com.mms.usercenter.common.message.vo.MsgDmMessageVo;

/**
 * 实现功能【私信服务】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
public interface MsgDmService {

    Page<MsgDmConversationVo> getConversationPage(MsgDmConversationPageQueryDto dto);

    MsgDmConversationVo openConversation(MsgDmOpenDto dto);

    /** 按会话 ID 打开（深链 / 铃铛）；若已隐藏则取消隐藏 */
    MsgDmConversationVo getConversationById(Long conversationId);

    Page<MsgDmMessageVo> getMessagePage(MsgDmMessagePageQueryDto dto);

    MsgDmMessageVo sendMessage(MsgDmSendDto dto);

    void hideConversation(Long conversationId);

    void pinConversation(Long conversationId, boolean pinned);

    void deleteConversation(Long conversationId);

    /** 当前用户全部私信未读清零 */
    void markAllRead();
}
