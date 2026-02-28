package com.mms.job.core;

import com.mms.common.core.response.Response;
import com.mms.common.core.utils.IdUtils;
import com.mms.common.job.dto.JobExecuteDto;
import com.mms.job.common.entity.JobEntity;
import com.mms.job.common.entity.JobRunLogEntity;
import com.mms.job.core.mapper.JobRunLogMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 实现功能【定时任务执行服务】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-25 11:07:14
 */
@Slf4j
@Service
public class JobExecuteService {

    /**
     * 定时任务线程池
     */
    @Resource(name = "schedulerTaskExecutor")
    private ThreadPoolTaskExecutor schedulerTaskExecutor;

    /**
     * 远程调用各业务服务使用的 RestTemplate
     */
    @Resource
    private RestTemplate restTemplate;

    /**
     * 执行记录 Mapper（直接写入执行日志，避免形成循环依赖）
     */
    @Resource
    private JobRunLogMapper jobRunLogMapper;

    /**
     * 同步执行任务（在当前线程中执行）
     *
     * @param job 任务定义实体
     */
    public void execute(JobEntity job) {
        if (job == null) {
            return;
        }
        // 未启用的任务直接跳过
        if (!Objects.equals(job.getEnabled(), 1)) {
            log.info("定时任务未启用，跳过执行，jobId={}，jobCode={}", job.getId(), job.getJobCode());
            return;
        }

        // 服务名和任务类型不能为空
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

        // 组装请求
        JobExecuteDto dto = new JobExecuteDto();
        dto.setJobType(jobType);
        dto.setParamsJson(job.getParamsJson());
        dto.setJobId(job.getId());
        String runId = IdUtils.timestampId();
        dto.setRequestId(runId);
        // 拼接url
        String url = "http://gateway/" + serviceName + "/internal/job/execute";

        long start = System.currentTimeMillis();
        // 记录执行开始日志
        JobRunLogEntity runLog = createStartRunLog(job, runId);
        try {
            log.info("开始定时任务远程调用，serviceName={}，url={}，jobId={}，jobCode={}，jobType={}", serviceName, url, job.getId(), job.getJobCode(), jobType);
            Response<?> response = restTemplate.postForObject(url, dto, Response.class);
            long cost = System.currentTimeMillis() - start;
            if (response == null) {
                log.error("定时任务远程调用返回为空，视为失败，jobId={}，jobCode={}，耗时={}ms", job.getId(), job.getJobCode(), cost);
                markFail(runLog.getId(), cost, "远程调用返回为空", getStackTrace(new RuntimeException("remote response is null")));
                return;
            }
            if (!Objects.equals(response.getCode(), Response.SUCCESS_CODE)) {
                log.error("定时任务远程调用失败，jobId={}，jobCode={}，耗时={}ms，错误信息={}", job.getId(), job.getJobCode(), cost, response.getMessage());
                String stack = getStackTrace(new RuntimeException("remote job failed, code=" + response.getCode() + ", message=" + response.getMessage()));
                markFail(runLog.getId(), cost, response.getMessage(), stack);
                return;
            }
            log.info("定时任务远程调用成功，jobId={}，jobCode={}，耗时={}ms", job.getId(), job.getJobCode(), cost);
            markSuccess(runLog.getId(), cost);
        } catch (Exception e) {
            long cost = System.currentTimeMillis() - start;
            log.error("定时任务远程调用异常，jobId={}，jobCode={}，耗时={}ms，错误：{}", job.getId(), job.getJobCode(), cost, e.getMessage(), e);
            markFail(runLog.getId(), cost, e.getMessage(), getStackTrace(e));
            // TODO: 后续可以在这里失败重试、告警通知等
        }
    }

    /**
     * 异步提交任务到线程池执行
     */
    public void submitAsync(JobEntity job) {
        if (job == null) {
            return;
        }
        schedulerTaskExecutor.submit(() -> execute(job));
    }

    /**
     * 创建并插入一条“开始执行”的运行日志
     */
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

    /**
     * 标记执行成功（仅当当前状态仍为 running 时才更新，避免覆盖人工终止等状态）
     */
    private void markSuccess(Long logId, long durationMs) {
        JobRunLogEntity current = jobRunLogMapper.selectById(logId);
        if (current == null) {
            log.warn("标记执行成功时未找到执行记录，logId={}", logId);
            return;
        }
        if (!"running".equals(current.getStatus())) {
            log.info("执行记录当前状态为 {}，不再覆盖为 success，logId={}", current.getStatus(), logId);
            return;
        }
        JobRunLogEntity entity = new JobRunLogEntity();
        entity.setId(logId);
        entity.setStatus("success");
        entity.setEndTime(LocalDateTime.now());
        entity.setDurationMs(durationMs);
        jobRunLogMapper.updateById(entity);
    }

    /**
     * 标记执行失败（仅当当前状态仍为 running 时才更新，避免覆盖人工终止等状态）
     */
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

    /**
     * 将异常堆栈转换为字符串，方便存入数据库
     */
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