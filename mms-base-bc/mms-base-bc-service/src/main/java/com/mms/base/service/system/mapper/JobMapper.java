package com.mms.base.service.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.system.dto.JobPageQueryDto;
import com.mms.base.common.system.entity.JobEntity;
import com.mms.base.common.system.vo.JobVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 实现功能【定时任务实体 Mapper】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-11 10:10:13
 */
@Mapper
public interface JobMapper extends BaseMapper<JobEntity> {

    /**
     * 分页查询定时任务列表
     *
     * @param page 分页参数
     * @param dto  查询条件
     * @return 分页结果
     */
    Page<JobVo> getJobPage(Page<JobVo> page, @Param("dto") JobPageQueryDto dto);
}