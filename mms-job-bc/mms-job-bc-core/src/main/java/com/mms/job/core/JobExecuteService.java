package com.mms.job.core;

import com.mms.common.mq.api.constants.MqTagConstants;
import com.mms.common.mq.api.constants.MqTopicConstants;
import com.mms.common.mq.api.enums.MqSendStatus;
import com.mms.common.mq.api.message.MqMessage;
import com.mms.common.mq.api.service.MqSendService;
import com.mms.common.job.dto.JobExecuteDispatchPayload;
import com.mms.job.common.entity.JobEntity;
import com.mms.job.common.entity.JobRunLogEntity;
import com.mms.job.core.mapper.JobRunLogMapper;
import com.mms.common.core.utils.IdUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 实现功能【定时任务执行服务】
 * <p>
 * 通过 MQ 将执行指令投递至目标业务服务（Tag 为 serviceName），结果由 {@code JobExecuteResultMqListener} 异步回写。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-02-25 11:07:14
 */
@Slf4j
@Service
public class JobExecuteService {

    @Resource(name = "schedulerTaskExecutor")
    private ThreadPoolTaskExecutor schedulerTaskExecutor;

    @Resource
    private MqSendService mqSendService;

    @Resource
    private JobRunLogMapper jobRunLogMapper;

    /**
     * 投递任务执行消息至目标业务服务
     */
    public void execute(JobEntity job) {
        if (job == null) {
            return;
        }
        if (!Objects.equals(job.getEnabled(), 1)) {
            log.info("定时任务未启用，跳过执行，jobId={}，jobCode={}", job.getId(), job.getJobCode());
            return;
        }

        String serviceName = job.getServiceName();
        String jobType = job.getJobType();
        if (!StringUtils.hasText(serviceName)) {
            log.warn("任务所属服务（serviceName）为空，跳过执行，jobId={}，jobCode={}", job.getId(), job.getJobCode());
            return;
        }
        if (!StringUtils.hasText(jobType)) {
            log.warn("任务类型（jobType）为空，跳过执行，jobId={}，jobCode={}", job.getId(), job.getJobCode());
            return;
        }

        String runId = IdUtils.timestampId();
        JobRunLogEntity runLog = createStartRunLog(job, runId);

        JobExecuteDispatchPayload payload = new JobExecuteDispatchPayload();
        payload.setJobId(job.getId());
        payload.setJobType(jobType);
        payload.setParamsJson(job.getParamsJson());
        payload.setRequestId(runId);
        payload.setRunLogId(runLog.getId());

        MqMessage<JobExecuteDispatchPayload> message = MqMessage.<JobExecuteDispatchPayload>builder()
                .eventType(MqTagConstants.JOB_EXECUTE)
                .messageKey(runId)
                .payload(payload)
                .build();

        try {
            log.info("投递定时任务 MQ，serviceName={}，jobId={}，jobCode={}，jobType={}，runLogId={}",
                    serviceName, job.getId(), job.getJobCode(), jobType, runLog.getId());
            var sendResult = mqSendService.send(MqTopicConstants.JOB, serviceName, message);
            if (sendResult.getStatus() == MqSendStatus.SKIPPED) {
                log.warn("MQ 未启用，任务未实际投递，jobId={}，jobCode={}", job.getId(), job.getJobCode());
                markFail(runLog.getId(), 0L, "MQ 未启用，无法投递任务执行消息", null);
                return;
            }
            if (sendResult.getStatus() != MqSendStatus.SUCCESS) {
                markFail(runLog.getId(), 0L, "MQ 投递失败", null);
            }
        } catch (Exception e) {
            log.error("定时任务 MQ 投递异常，jobId={}，jobCode={}，错误：{}", job.getId(), job.getJobCode(), e.getMessage(), e);
            markFail(runLog.getId(), 0L, e.getMessage(), getStackTrace(e));
        }
    }

    public void submitAsync(JobEntity job) {
        if (job == null) {
            return;
        }
        schedulerTaskExecutor.submit(() -> execute(job));
    }

    private JobRunLogEntity createStartRunLog(JobEntity job, String runId) {
        JobRunLogEntity logEntity = new JobRunLogEntity();
        logEntity.setJobId(job.getId());
        logEntity.setJobName(job.getJobName());
        logEntity.setRunId(runId);
        logEntity.setStatus("running");
        logEntity.setStartTime(LocalDateTime.now());
        logEntity.setInstanceId(getInstanceId());
        logEntity.setHost(getHost());
        jobRunLogMapper.insert(logEntity);
        return logEntity;
    }

    private void markFail(Long logId, long durationMs, String errorMsg, String errorStack) {
        JobRunLogEntity current = jobRunLogMapper.selectById(logId);
        if (current == null) {
            log.warn("标记执行失败时未找到执行记录，logId={}", logId);
            return;
        }
        if (!"running".equals(current.getStatus())) {
            log.info("执行记录当前状态为 {}，不再覆盖为 fail，logId={}", current.getStatus(), logId);
            return;
        }
        JobRunLogEntity entity = new JobRunLogEntity();
        entity.setId(logId);
        entity.setStatus("fail");
        entity.setEndTime(LocalDateTime.now());
        entity.setDurationMs(durationMs);
        entity.setErrorMessage(errorMsg);
        entity.setErrorStack(errorStack);
        jobRunLogMapper.updateById(entity);
    }

    private String getInstanceId() {
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

    private String getStackTrace(Throwable e) {
        if (e == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(e).append("\n");
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append("\tat ").append(element).append("\n");
        }
        return sb.toString();
    }
}
