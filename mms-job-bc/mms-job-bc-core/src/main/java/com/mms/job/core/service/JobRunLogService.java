package com.mms.job.core.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.job.common.dto.JobRunLogPageQueryDto;
import com.mms.job.common.entity.JobEntity;
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
     * 创建一条执行记录，状态置为 RUNNING
     *
     * @param job   任务定义
     * @param runId 本次执行唯一 ID
     * @return 新建的执行记录实体
     */
    JobRunLogEntity startRun(JobEntity job, String runId);

    /**
     * 标记执行成功
     *
     * @param logId      执行记录 ID
     * @param durationMs 耗时毫秒
     */
    void markSuccess(Long logId, long durationMs);

    /**
     * 标记执行失败
     *
     * @param logId       执行记录 ID
     * @param durationMs  耗时毫秒
     * @param errorMsg    错误摘要
     * @param errorStack  错误堆栈
     */
    void markFail(Long logId, long durationMs, String errorMsg, String errorStack);

    /**
     * 分页查询任务执行记录
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    Page<JobRunLogEntity> getJobRunLogPage(JobRunLogPageQueryDto dto);
}