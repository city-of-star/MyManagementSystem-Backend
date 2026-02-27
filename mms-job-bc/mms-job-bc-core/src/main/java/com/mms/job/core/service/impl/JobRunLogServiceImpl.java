package com.mms.job.core.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import com.mms.job.common.dto.JobRunLogBatchDeleteDto;
import com.mms.job.common.dto.JobRunLogPageQueryDto;
import com.mms.job.common.entity.JobEntity;
import com.mms.job.common.entity.JobRunLogEntity;
import com.mms.job.core.mapper.JobRunLogMapper;
import com.mms.job.core.service.JobRunLogService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        try {
            log.info("分页查询定时任务执行记录，参数：{}", dto);
            Page<JobRunLogEntity> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            return jobRunLogMapper.getJobRunLogPage(page, dto);
        } catch (Exception e) {
            log.error("分页查询定时任务执行记录失败：{}", e.getMessage(), e);
            throw new ServerException("分页查询定时任务执行记录失败", e);
        }

    }

    @Override
    public JobRunLogEntity getJobRunLogById(Long logId) {
        try {
            log.info("根据ID查询定时任务执行记录，logId：{}", logId);
            if (logId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "执行记录ID不能为空");
            }
            JobRunLogEntity logEntity = jobRunLogMapper.selectById(logId);
            if (logEntity == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "定时任务执行记录不存在");
            }
            return logEntity;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("根据ID查询定时任务执行记录失败：{}", e.getMessage(), e);
            throw new ServerException("查询定时任务执行记录失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJobRunLog(Long logId) {
        try {
            log.info("删除定时任务执行记录，logId：{}", logId);
            if (logId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "执行记录ID不能为空");
            }
            JobRunLogEntity logEntity = jobRunLogMapper.selectById(logId);
            if (logEntity == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "定时任务执行记录不存在");
            }
            jobRunLogMapper.deleteById(logId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除定时任务执行记录失败：{}", e.getMessage(), e);
            throw new ServerException("删除定时任务执行记录失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteJobRunLog(JobRunLogBatchDeleteDto dto) {
        try {
            log.info("批量删除定时任务执行记录，logIds：{}", dto.getLogIds());
            if (dto.getLogIds() == null || dto.getLogIds().isEmpty()) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "执行记录ID列表不能为空");
            }
            for (Long logId : dto.getLogIds()) {
                deleteJobRunLog(logId);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量删除定时任务执行记录失败：{}", e.getMessage(), e);
            throw new ServerException("批量删除定时任务执行记录失败", e);
        }
    }

    @Override
    public void exportJobRunLog(JobRunLogPageQueryDto dto) {
        // TODO: 后续集成 Excel 导出功能时实现
        log.warn("导出定时任务执行记录功能暂未实现，参数：{}", dto);
        throw new BusinessException(ErrorCode.INVALID_OPERATION, "定时任务执行记录导出功能暂未实现");
    }

    @Override
    public void retryJobRun(Long logId) {
        // TODO: 后续根据业务需求实现重试逻辑
        log.warn("重试执行定时任务功能暂未实现，logId：{}", logId);
        throw new BusinessException(ErrorCode.INVALID_OPERATION, "定时任务执行记录重试执行功能暂未实现");
    }

    @Override
    public void terminateJobRun(Long logId) {
        // TODO: 后续根据业务需求实现终止逻辑
        log.warn("终止执行定时任务功能暂未实现，logId：{}", logId);
        throw new BusinessException(ErrorCode.INVALID_OPERATION, "定时任务执行记录终止执行功能暂未实现");
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