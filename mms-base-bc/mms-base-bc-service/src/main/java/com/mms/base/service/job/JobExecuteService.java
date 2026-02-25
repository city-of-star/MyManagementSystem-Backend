package com.mms.base.service.job;

import com.mms.base.common.system.entity.JobEntity;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

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

    @Resource
    private JobHandlerRegistry jobHandlerRegistry;

    /**
     * 定时任务线程池
     */
    @Resource(name = "schedulerTaskExecutor")
    private ThreadPoolTaskExecutor schedulerTaskExecutor;

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

        String jobCode = job.getJobCode();
        JobHandler handler = jobHandlerRegistry.getHandler(jobCode);
        if (handler == null) {
            log.warn("未找到定时任务处理器，跳过执行，jobId={}，jobCode={}", job.getId(), jobCode);
            return;
        }

        long start = System.currentTimeMillis();
        try {
            log.info("开始执行定时任务，jobId={}，jobCode={}", job.getId(), jobCode);
            handler.execute(job.getParamsJson());
            long cost = System.currentTimeMillis() - start;
            log.info("定时任务执行完成，jobId={}，jobCode={}，耗时={}ms", job.getId(), jobCode, cost);
        } catch (Exception e) {
            long cost = System.currentTimeMillis() - start;
            log.error("定时任务执行失败，jobId={}，jobCode={}，耗时={}ms，错误：{}", job.getId(), jobCode, cost, e.getMessage(), e);
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