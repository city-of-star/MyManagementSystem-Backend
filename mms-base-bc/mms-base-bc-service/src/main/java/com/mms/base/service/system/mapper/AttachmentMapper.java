package com.mms.base.service.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.system.dto.AttachmentPageQueryDto;
import com.mms.base.common.system.entity.AttachmentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 实现功能【附件实体 Mapper】
 *
 * @author li.hongyu
 * @date 2026-02-06 14:36:38
 */
@Mapper
public interface AttachmentMapper extends BaseMapper<AttachmentEntity> {

    /**
     * 分页查询附件列表（XML）
     *
     * @param page 分页参数
     * @param dto  查询条件
     * @return 分页结果
     */
    Page<AttachmentEntity> getAttachmentPage(Page<AttachmentEntity> page, @Param("dto") AttachmentPageQueryDto dto);
}

