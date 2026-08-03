package com.mms.usercenter.service.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.usercenter.common.message.dto.MsgAnnounceUserPageQueryDto;
import com.mms.usercenter.common.message.entity.MsgSysInboxEntity;
import com.mms.usercenter.common.message.vo.MsgAnnounceUserVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 实现功能【系统收件箱 Mapper】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
@Mapper
public interface MsgSysInboxMapper extends BaseMapper<MsgSysInboxEntity> {

    Page<MsgAnnounceUserVo> pageAnnounceUsers(@Param("page") Page<MsgAnnounceUserVo> page,
                                              @Param("announceId") Long announceId,
                                              @Param("readFlag") Integer readFlag,
                                              @Param("dto") MsgAnnounceUserPageQueryDto dto);
}
