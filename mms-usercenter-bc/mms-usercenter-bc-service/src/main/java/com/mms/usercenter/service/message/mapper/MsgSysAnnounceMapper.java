package com.mms.usercenter.service.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.usercenter.common.message.dto.MsgAnnouncePageQueryDto;
import com.mms.usercenter.common.message.entity.MsgSysAnnounceEntity;
import com.mms.usercenter.common.message.vo.MsgAnnounceVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 实现功能【系统公告 Mapper】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
@Mapper
public interface MsgSysAnnounceMapper extends BaseMapper<MsgSysAnnounceEntity> {

    /**
     * 分页查询系统公告列表
     */
    Page<MsgAnnounceVo> getAnnouncePage(@Param("page") Page<MsgAnnounceVo> page, @Param("dto") MsgAnnouncePageQueryDto dto);

    /**
     * 根据ID查询系统公告详情，不存在返回 null
     */
    MsgAnnounceVo getAnnounceById(@Param("id") Long id);

    /**
     * 状态 CAS：当前状态匹配且未删除时才更新（扇出抢锁）；返回影响行数，0 表示抢锁失败
     */
    int casStatus(@Param("id") Long id, @Param("fromStatus") int fromStatus, @Param("toStatus") int toStatus);
}
