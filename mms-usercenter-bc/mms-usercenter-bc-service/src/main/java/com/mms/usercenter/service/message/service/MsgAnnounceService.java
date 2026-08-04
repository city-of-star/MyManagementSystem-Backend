package com.mms.usercenter.service.message.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.usercenter.common.message.dto.MsgAnnounceCreateDto;
import com.mms.usercenter.common.message.dto.MsgAnnouncePageQueryDto;
import com.mms.usercenter.common.message.dto.MsgAnnounceUpdateDto;
import com.mms.usercenter.common.message.dto.MsgAnnounceUserPageQueryDto;
import com.mms.usercenter.common.message.vo.MsgAnnounceUserVo;
import com.mms.usercenter.common.message.vo.MsgAnnounceVo;

/**
 * 实现功能【系统公告服务】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
public interface MsgAnnounceService {

    Page<MsgAnnounceVo> getAnnouncePage(MsgAnnouncePageQueryDto dto);

    MsgAnnounceVo getAnnounceById(Long id);

    MsgAnnounceVo createAnnounce(MsgAnnounceCreateDto dto);

    MsgAnnounceVo updateAnnounce(Long id, MsgAnnounceUpdateDto dto);

    void recallAnnounce(Long id);

    void deleteAnnounce(Long id);

    Page<MsgAnnounceUserVo> pageReadUsers(Long announceId, MsgAnnounceUserPageQueryDto dto);

    Page<MsgAnnounceUserVo> pageUnreadUsers(Long announceId, MsgAnnounceUserPageQueryDto dto);
}
