package com.mms.usercenter.service.message.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.usercenter.common.message.dto.MsgAnnounceCreateDto;
import com.mms.usercenter.common.message.dto.MsgAnnouncePageQueryDto;
import com.mms.usercenter.common.message.dto.MsgAnnounceUpdateDto;
import com.mms.usercenter.common.message.dto.MsgAnnounceUserPageQueryDto;
import com.mms.usercenter.common.message.vo.MsgAnnounceUserVo;
import com.mms.usercenter.common.message.vo.MsgAnnounceVo;
import com.mms.usercenter.common.message.vo.MsgLinkOptionVo;

import java.util.List;

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

    /**
     * 分页查询公告列表
     */
    Page<MsgAnnounceVo> getAnnouncePage(MsgAnnouncePageQueryDto dto);

    /**
     * 根据公告ID获取公告详情
     */
    MsgAnnounceVo getAnnounceById(Long id);

    /**
     * 跳转页面选项（启用目录/菜单树，仅有 path 的菜单可选）
     */
    List<MsgLinkOptionVo> listLinkOptions();

    /**
     * 新建公告
     */
    MsgAnnounceVo createAnnounce(MsgAnnounceCreateDto dto);

    /**
     * 修改公告
     */
    MsgAnnounceVo updateAnnounce(Long id, MsgAnnounceUpdateDto dto);

    /**
     * 撤回公告（收件箱里面不会显示此公告，但是公告管理里面仍能看到）
     */
    void recallAnnounce(Long id);

    /**
     * 删除公告（先撤回再逻辑删除）
     */
    void deleteAnnounce(Long id);

    /**
     * 分页查询此公告的已阅读人员
     */
    Page<MsgAnnounceUserVo> pageReadUsers(Long announceId, MsgAnnounceUserPageQueryDto dto);

    /**
     * 分页查询此公告的未阅读人员
     */
    Page<MsgAnnounceUserVo> pageUnreadUsers(Long announceId, MsgAnnounceUserPageQueryDto dto);
}
