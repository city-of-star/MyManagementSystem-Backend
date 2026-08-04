package com.mms.usercenter.service.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.usercenter.common.message.dto.MsgAnnounceUserPageQueryDto;
import com.mms.usercenter.common.message.entity.MsgSysInboxEntity;
import com.mms.usercenter.common.message.vo.MsgAnnounceReadStatVo;
import com.mms.usercenter.common.message.vo.MsgAnnounceUserVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Collection;
import java.util.List;

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

    /**
     * 批量统计公告已读/未读（避免列表 N+1）。
     */
    List<MsgAnnounceReadStatVo> countReadStatsByAnnounceIds(@Param("announceIds") Collection<Long> announceIds);

    /**
     * 含逻辑删除行（用于扇出幂等：用户删过收件后再扇出需恢复）
     */
    @Select("""
            SELECT id, user_id, announce_id, biz_type, biz_id, title, content_html, content_text,
                   starred, read_flag, read_time, deleted, create_by, create_time, update_by, update_time
            FROM msg_sys_inbox
            WHERE announce_id = #{announceId}
              AND user_id = #{userId}
            LIMIT 1
            """)
    MsgSysInboxEntity selectByAnnounceUserIncludeDeleted(@Param("announceId") Long announceId,
                                                         @Param("userId") Long userId);

    @Update("""
            UPDATE msg_sys_inbox
            SET deleted = 0,
                read_flag = 0,
                read_time = NULL,
                starred = 0,
                title = #{title},
                content_html = #{contentHtml},
                content_text = #{contentText},
                update_time = NOW()
            WHERE id = #{id}
            """)
    int restoreDeletedInbox(@Param("id") Long id,
                            @Param("title") String title,
                            @Param("contentHtml") String contentHtml,
                            @Param("contentText") String contentText);
}
