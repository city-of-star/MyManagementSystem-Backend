package com.mms.job.core;

import com.mms.common.core.response.Response;
import com.mms.common.core.utils.IdUtils;
import com.mms.common.job.dto.JobExecuteDto;
import com.mms.job.common.entity.JobEntity;
import com.mms.job.common.entity.JobRunLogEntity;
import com.mms.job.core.service.JobRunLogService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

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

    @Resource
    private JobRunLogService jobRunLogService;

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
        JobRunLogEntity runLog = jobRunLogService.startRun(job, runId);
        try {
            log.info("开始定时任务远程调用，serviceName={}，url={}，jobId={}，jobCode={}，jobType={}", serviceName, url, job.getId(), job.getJobCode(), jobType);
            Response<?> response = restTemplate.postForObject(url, dto, Response.class);
            long cost = System.currentTimeMillis() - start;
            if (response == null) {
                log.error("定时任务远程调用返回为空，视为失败，jobId={}，jobCode={}，耗时={}ms", job.getId(), job.getJobCode(), cost);
                jobRunLogService.markFail(runLog.getId(), cost, "远程调用返回为空", null);
                return;
            }
            if (!Objects.equals(response.getCode(), Response.SUCCESS_CODE)) {
                log.error("定时任务远程调用失败，jobId={}，jobCode={}，耗时={}ms，错误信息={}", job.getId(), job.getJobCode(), cost, response.getMessage());
                jobRunLogService.markFail(runLog.getId(), cost, response.getMessage(), null);
                return;
            }
            log.info("定时任务远程调用成功，jobId={}，jobCode={}，耗时={}ms", job.getId(), job.getJobCode(), cost);
            jobRunLogService.markSuccess(runLog.getId(), cost);
        } catch (Exception e) {
            long cost = System.currentTimeMillis() - start;
            log.error("定时任务远程调用异常，jobId={}，jobCode={}，耗时={}ms，错误：{}", job.getId(), job.getJobCode(), cost, e.getMessage(), e);
            jobRunLogService.markFail(runLog.getId(), cost, e.getMessage(), getStackTrace(e));
            // TODO: 后续可以在这里记录执行日志表、失败次数、告警通知等
        }
    }

    private String getStackTrace(Exception e) {
        StringBuilder sb = new StringBuilder();
        sb.append(e.toString()).append("\n");
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append("\tat ").append(element).append("\n");
        }
        return sb.toString();
    }

    /**
     * 异步提交任务到线程池执行
     *
     * @param job 任务定义实体
     */
    public void submitAsync(JobEntity job) {
        if (job == null) {
            return;
        }
        schedulerTaskExecutor.submit(() -> execute(job));
    }
}