package com.mms.job.core.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import com.mms.job.common.dto.JobRunLogBatchDeleteDto;
import com.mms.job.common.dto.JobRunLogPageQueryDto;
import com.mms.job.common.entity.JobEntity;
import com.mms.job.common.entity.JobRunLogEntity;
import com.mms.job.core.JobExecuteService;
import com.mms.job.core.mapper.JobMapper;
import com.mms.job.core.mapper.JobRunLogMapper;
import com.mms.job.core.service.JobRunLogService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
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

    @Resource
    private JobMapper jobMapper;

    @Resource
    private JobExecuteService jobExecuteService;


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
        try {
            log.info("尝试重试执行定时任务，logId：{}", logId);
            if (logId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "执行记录ID不能为空");
            }
            JobRunLogEntity logEntity = jobRunLogMapper.selectById(logId);
            if (logEntity == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "定时任务执行记录不存在");
            }
            // 仅当状态为 FAIL / TIMEOUT / SKIP 时允许重试
            String status = logEntity.getStatus();
            if (!"FAIL".equals(status) && !"TIMEOUT".equals(status) && !"SKIP".equals(status)) {
                throw new BusinessException(ErrorCode.INVALID_OPERATION, "仅失败、超时或已跳过的执行记录支持重试");
            }
            Long jobId = logEntity.getJobId();
            if (jobId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "执行记录未关联任务ID，无法重试");
            }
            JobEntity job = jobMapper.selectById(jobId);
            if (job == null || Objects.equals(job.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "关联的定时任务不存在或已删除");
            }
            // 异步提交执行一次，会生成新的执行记录
            jobExecuteService.submitAsync(job);
            log.info("已提交重试执行请求，logId={}，jobId={}", logId, jobId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("重试执行定时任务失败，logId={}，错误：{}", logId, e.getMessage(), e);
            throw new ServerException("重试执行定时任务失败", e);
        }
    }

    @Override
    public void terminateJobRun(Long logId) {
        try {
            log.info("尝试终止定时任务执行，logId：{}", logId);
            if (logId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "执行记录ID不能为空");
            }
            JobRunLogEntity logEntity = jobRunLogMapper.selectById(logId);
            if (logEntity == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "定时任务执行记录不存在");
            }
            // 仅 RUNNING 状态允许终止
            if (!Objects.equals(logEntity.getStatus(), "RUNNING")) {
                throw new BusinessException(ErrorCode.INVALID_OPERATION, "仅运行中的执行记录支持终止");
            }
            LocalDateTime now = LocalDateTime.now();
            Long durationMs = null;
            if (logEntity.getStartTime() != null) {
                durationMs = Duration.between(logEntity.getStartTime(), now).toMillis();
            }
            JobRunLogEntity update = new JobRunLogEntity();
            update.setId(logId);
            // 这里用 SKIP 状态表示“被人工终止/跳过”
            update.setStatus("SKIP");
            update.setEndTime(now);
            update.setDurationMs(durationMs);
            update.setErrorMessage("执行被人工终止");
            jobRunLogMapper.updateById(update);
            log.info("已将执行记录标记为 SKIP（人工终止），logId={}", logId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("终止定时任务执行失败，logId={}，错误：{}", logId, e.getMessage(), e);
            throw new ServerException("终止定时任务执行失败", e);
        }
    }

}