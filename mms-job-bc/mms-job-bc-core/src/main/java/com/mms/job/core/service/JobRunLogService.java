package com.mms.job.core.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.job.common.dto.JobRunLogBatchDeleteDto;
import com.mms.job.common.dto.JobRunLogPageQueryDto;
import com.mms.job.common.entity.JobRunLogEntity;

/**
 * 实现功能【定时任务执行记录】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-27 16:08:50
 */
public interface JobRunLogService {

    /**
     * 分页查询任务执行记录
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    Page<JobRunLogEntity> getJobRunLogPage(JobRunLogPageQueryDto dto);

    /**
     * 根据执行记录ID查询详情
     *
     * @param logId 执行记录ID
     * @return 执行记录详情
     */
    JobRunLogEntity getJobRunLogById(Long logId);

    /**
     * 删除单条执行记录
     *
     * @param logId 执行记录ID
     */
    void deleteJobRunLog(Long logId);

    /**
     * 批量删除执行记录
     *
     * @param dto 批量删除参数
     */
    void batchDeleteJobRunLog(JobRunLogBatchDeleteDto dto);

    /**
     * 导出执行记录（TODO：后续集成 Excel 功能后实现）
     *
     * @param dto 查询条件
     */
    void exportJobRunLog(JobRunLogPageQueryDto dto);

    /**
     * 重试执行（TODO：后续补充实现）
     *
     * @param logId 执行记录ID
     */
    void retryJobRun(Long logId);

    /**
     * 终止执行（TODO：后续补充实现）
     *
     * @param logId 执行记录ID
     */
    void terminateJobRun(Long logId);
}