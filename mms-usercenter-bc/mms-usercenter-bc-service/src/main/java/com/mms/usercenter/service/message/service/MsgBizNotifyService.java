package com.mms.usercenter.service.message.service;

import com.mms.usercenter.common.message.dto.MsgBizNotifyDto;
import com.mms.usercenter.common.message.vo.MsgBizNotifyVo;

/**
 * 实现功能【业务系统通知服务】
 *
 * @author li.hongyu
 * @date 2026-08-06
 */
public interface MsgBizNotifyService {

    /**
     * 投递业务系统通知（幂等：bizType + bizId + userId）
     */
    MsgBizNotifyVo notify(MsgBizNotifyDto dto);
}
