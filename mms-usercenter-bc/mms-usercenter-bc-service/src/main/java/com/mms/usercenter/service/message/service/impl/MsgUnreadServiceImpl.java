package com.mms.usercenter.service.message.service.impl;

import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.webmvc.utils.UserContextUtils;
import com.mms.usercenter.common.message.vo.MsgUnreadVo;
import com.mms.usercenter.service.message.service.MsgUnreadService;
import com.mms.usercenter.service.message.support.MsgUnreadSupport;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 实现功能【消息未读服务实现】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
@Service
public class MsgUnreadServiceImpl implements MsgUnreadService {

    @Resource
    private MsgUnreadSupport msgUnreadSupport;

    @Override
    public MsgUnreadVo getCurrentUnread() {
        Long userId = UserContextUtils.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.LOGIN_EXPIRED, "未登录");
        }
        return msgUnreadSupport.getUnread(userId);
    }
}
