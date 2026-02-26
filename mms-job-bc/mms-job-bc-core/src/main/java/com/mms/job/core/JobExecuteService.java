package com.mms.job.core;

import com.mms.common.job.dto.JobExecuteRequest;
import com.mms.common.job.dto.JobExecuteResponse;
import com.mms.job.common.entity.JobEntity;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.UUID;

/**
 * 实现功能【定时任务执行服务】
 * <p>
 * Job 调度中心：根据任务定义，远程调用各业务服务的通用执行入口 /internal/job/execute。
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
     * <p>
     * 建议在公共模块中声明为 @LoadBalanced RestTemplate，支持服务名调用。
     */
    @Resource
    private RestTemplate restTemplate;

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

        // 路由目标服务 & 任务 key（使用 jobType，与 @JobDefinition 注册保持一致）
        String serviceName = job.getServiceName();
        String jobKey = job.getJobType();
        if (!StringUtils.hasText(serviceName)) {
            log.warn("任务所属服务 serviceName 为空，跳过执行，jobId={}，jobCode={}", job.getId(), job.getJobCode());
            return;
        }
        if (!StringUtils.hasText(jobKey)) {
            log.warn("任务类型 jobType 为空，跳过执行，jobId={}，jobCode={}", job.getId(), job.getJobCode());
            return;
        }

        // 组装请求
        JobExecuteRequest request = new JobExecuteRequest();
        request.setJobKey(jobKey);
        request.setParamsJson(job.getParamsJson());
        request.setJobId(job.getId());
        request.setRequestId(UUID.randomUUID().toString());

        String url = "http://" + serviceName + "/internal/job/execute";

        long start = System.currentTimeMillis();
        try {
            log.info("开始远程执行定时任务，serviceName={}，url={}，jobId={}，jobCode={}，jobType={}",
                    serviceName, url, job.getId(), job.getJobCode(), jobKey);

            JobExecuteResponse response = restTemplate.postForObject(url, request, JobExecuteResponse.class);

            long cost = System.currentTimeMillis() - start;
            if (response == null) {
                log.error("定时任务执行返回为空，视为失败，jobId={}，jobCode={}，耗时={}ms", job.getId(), job.getJobCode(), cost);
                return;
            }
            if (!response.isSuccess()) {
                log.error("定时任务执行失败，jobId={}，jobCode={}，耗时={}ms，错误信息={}",
                        job.getId(), job.getJobCode(), cost, response.getMessage());
                return;
            }

            log.info("定时任务执行成功，jobId={}，jobCode={}，耗时={}ms", job.getId(), job.getJobCode(), cost);
        } catch (Exception e) {
            long cost = System.currentTimeMillis() - start;
            log.error("定时任务远程调用异常，jobId={}，jobCode={}，耗时={}ms，错误：{}",
                    job.getId(), job.getJobCode(), cost, e.getMessage(), e);
            // TODO: 后续可以在这里记录执行日志表、失败次数、告警通知等
        }
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