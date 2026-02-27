package com.mms.job.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.job.common.dto.JobRunLogPageQueryDto;
import com.mms.job.common.entity.JobRunLogEntity;

/**
 * 实现功能【定时任务执行记录实体 Mapper】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-27 16:05:55
 */
public interface JobRunLogMapper extends BaseMapper<JobRunLogEntity> {

    /**
     * 分页查询任务执行记录
     *
     * @param page 分页参数
     * @param dto  查询条件
     * @return 分页结果
     */
    Page<JobRunLogEntity> getJobRunLogPage(Page<JobRunLogEntity> page, JobRunLogPageQueryDto dto);
}