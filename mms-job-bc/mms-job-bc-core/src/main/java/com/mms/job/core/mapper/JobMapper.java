package com.mms.job.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.job.common.dto.JobPageQueryDto;
import com.mms.job.common.entity.JobEntity;
import com.mms.job.common.vo.JobVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

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

    /**
     * 查询到点需要执行的任务
     *
     * @param now 当前时间
     */
    List<JobEntity> selectDueJobs(@Param("now") LocalDateTime now);

    /**
     * 仅更新 next_run_time
     *
     * @param id          任务ID
     * @param nextRunTime 下一次触发时间
     */
    int updateNextRunTime(@Param("id") Long id, @Param("nextRunTime") LocalDateTime nextRunTime);
}