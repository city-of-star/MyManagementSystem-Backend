package com.mms.job.core.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.job.common.dto.JobRunLogPageQueryDto;
import com.mms.job.common.entity.JobEntity;
import com.mms.job.common.entity.JobRunLogEntity;
import com.mms.job.core.mapper.JobRunLogMapper;
import com.mms.job.core.service.JobRunLogService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

/**
 * 实现功能【定时任务执行记录实现类】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-27 16:08:56
 */
@Slf4j
@Service
public class JobRunLogServiceImpl implements JobRunLogService {

    @Resource
    private JobRunLogMapper jobRunLogMapper;

    @Override
    public JobRunLogEntity startRun(JobEntity job, String runId) {
        JobRunLogEntity logEntity = new JobRunLogEntity();
        logEntity.setJobId(job.getId());
        logEntity.setRunId(runId);
        logEntity.setStatus("RUNNING");
        logEntity.setStartTime(LocalDateTime.now());
        logEntity.setInstanceId(getInstanceId());
        logEntity.setHost(getHost());
        jobRunLogMapper.insert(logEntity);
        return logEntity;
    }

    @Override
    public void markSuccess(Long logId, long durationMs) {
        JobRunLogEntity entity = new JobRunLogEntity();
        entity.setId(logId);
        entity.setStatus("SUCCESS");
        entity.setEndTime(LocalDateTime.now());
        entity.setDurationMs(durationMs);
        jobRunLogMapper.updateById(entity);
    }

    @Override
    public void markFail(Long logId, long durationMs, String errorMsg, String errorStack) {
        JobRunLogEntity entity = new JobRunLogEntity();
        entity.setId(logId);
        entity.setStatus("FAIL");
        entity.setEndTime(LocalDateTime.now());
        entity.setDurationMs(durationMs);
        entity.setErrorMessage(errorMsg);
        entity.setErrorStack(errorStack);
        jobRunLogMapper.updateById(entity);
    }

    @Override
    public Page<JobRunLogEntity> getJobRunLogPage(JobRunLogPageQueryDto dto) {
        Page<JobRunLogEntity> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return jobRunLogMapper.getJobRunLogPage(page, dto);
    }

    private String getInstanceId() {
        // 简单实现：使用主机名，后续可替换为更规范的实例标识
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            log.warn("获取实例ID失败，使用默认值", e);
            return "unknown-instance";
        }
    }

    private String getHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("获取主机IP失败，使用默认值", e);
            return "unknown-host";
        }
    }
}