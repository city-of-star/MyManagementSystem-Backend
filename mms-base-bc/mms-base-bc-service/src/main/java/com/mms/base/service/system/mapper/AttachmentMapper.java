package com.mms.base.service.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.system.dto.AttachmentPageQueryDto;
import com.mms.base.common.system.entity.AttachmentEntity;
import com.mms.base.common.system.vo.AttachmentVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 实现功能【附件实体 Mapper】
 *
 * @author li.hongyu
 * @date 2026-02-06 14:36:38
 */
@Mapper
public interface AttachmentMapper extends BaseMapper<AttachmentEntity> {

    /**
     * 分页查询附件列表
     *
     * @param page 分页参数
     * @param dto  查询条件
     * @return 分页结果
     */
    Page<AttachmentVo> getAttachmentPage(Page<AttachmentVo> page, @Param("dto") AttachmentPageQueryDto dto);

    /**
     * 查询一批【已逻辑删除】的附件记录，用于清理任务（物理删除）
     *
     * @param limit 本次最多查询多少条
     * @return 已逻辑删除的附件列表（最多 limit 条）
     */
    List<AttachmentEntity> selectDeletedForClean(@Param("limit") int limit);

    /**
     * 硬删除：真正删除数据库记录
     */
    int hardDeleteById(@Param("id") Long id);
}

