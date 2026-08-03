package com.mms.usercenter.service.message.support;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mms.common.core.utils.DateUtils;
import com.mms.common.websocket.common.protocol.WsMessage;
import com.mms.common.websocket.push.service.WsPushService;
import com.mms.usercenter.common.message.constants.MsgConstants;
import com.mms.usercenter.common.message.entity.MsgDmMemberEntity;
import com.mms.usercenter.common.message.entity.MsgSysInboxEntity;
import com.mms.usercenter.common.message.vo.MsgUnreadVo;
import com.mms.usercenter.service.message.mapper.MsgDmMemberMapper;
import com.mms.usercenter.service.message.mapper.MsgSysInboxMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * 实现功能【未读数统计与 WS 推送】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
@Component
public class MsgUnreadSupport {

    @Resource
    private MsgSysInboxMapper msgSysInboxMapper;

    @Resource
    private MsgDmMemberMapper msgDmMemberMapper;

    @Resource
    private WsPushService wsPushService;

    public MsgUnreadVo getUnread(Long userId) {
        MsgUnreadVo vo = new MsgUnreadVo();
        int sys = countSysUnread(userId);
        int priv = countPrivUnread(userId);
        vo.setSysUnread(sys);
        vo.setPrivUnread(priv);
        vo.setTotal(sys + priv);
        return vo;
    }

    public void pushUnread(Long userId) {
        if (userId == null) {
            return;
        }
        MsgUnreadVo unread = getUnread(userId);
        wsPushService.pushToUser(String.valueOf(userId), WsMessage.builder()
                .type(MsgConstants.WS_TYPE_MSG_UNREAD)
                .data(unread)
                .timestamp(DateUtils.nowMillis())
                .build());
    }

    public void pushUnreadBatch(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        for (Long userId : userIds) {
            pushUnread(userId);
        }
    }

    private int countSysUnread(Long userId) {
        Long count = msgSysInboxMapper.selectCount(new LambdaQueryWrapper<MsgSysInboxEntity>()
                .eq(MsgSysInboxEntity::getUserId, userId)
                .eq(MsgSysInboxEntity::getReadFlag, 0));
        return count == null ? 0 : count.intValue();
    }

    private int countPrivUnread(Long userId) {
        List<MsgDmMemberEntity> members = msgDmMemberMapper.selectList(new LambdaQueryWrapper<MsgDmMemberEntity>()
                .eq(MsgDmMemberEntity::getUserId, userId)
                .eq(MsgDmMemberEntity::getHidden, 0)
                .gt(MsgDmMemberEntity::getUnreadCount, 0));
        int total = 0;
        for (MsgDmMemberEntity member : members) {
            if (member.getUnreadCount() != null) {
                total += member.getUnreadCount();
            }
        }
        return total;
    }
}
