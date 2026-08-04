package com.mms.usercenter.service.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mms.usercenter.common.message.entity.MsgSysAnnounceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

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
     * 状态 CAS：仅当当前状态匹配且未删除时更新（扇出抢锁 / 防删后继续写）。
     */
    @Update("""
            UPDATE msg_sys_announce
            SET status = #{toStatus},
                update_time = NOW()
            WHERE id = #{id}
              AND status = #{fromStatus}
              AND deleted = 0
            """)
    int casStatus(@Param("id") Long id,
                  @Param("fromStatus") int fromStatus,
                  @Param("toStatus") int toStatus);
}
