package com.mms.usercenter.service.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mms.usercenter.common.message.entity.MsgDmMemberEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

/**
 * 实现功能【私信成员态 Mapper】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
@Mapper
public interface MsgDmMemberMapper extends BaseMapper<MsgDmMemberEntity> {

    /**
     * 接收方未读原子自增，并刷新预览（同时取消隐藏）
     */
    @Update("""
            UPDATE msg_dm_member
            SET unread_count = IFNULL(unread_count, 0) + 1,
                hidden = 0,
                last_msg_id = #{lastMsgId},
                last_msg_preview = #{preview},
                last_msg_time = #{lastMsgTime},
                update_time = NOW()
            WHERE id = #{id}
            """)
    int incrUnreadAndPreview(@Param("id") Long id,
                             @Param("lastMsgId") Long lastMsgId,
                             @Param("preview") String preview,
                             @Param("lastMsgTime") LocalDateTime lastMsgTime);

    /**
     * 当前用户全部私信会话标记已读
     */
    @Update("""
            UPDATE msg_dm_member
            SET unread_count = 0,
                last_read_msg_id = GREATEST(IFNULL(last_read_msg_id, 0), IFNULL(last_msg_id, 0)),
                update_time = NOW()
            WHERE user_id = #{userId}
              AND deleted = 0
              AND IFNULL(unread_count, 0) > 0
            """)
    int markAllReadByUserId(@Param("userId") Long userId);
}
