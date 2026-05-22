package com.mms.job.core;

import com.mms.job.common.entity.JobEntity;
import com.mms.job.core.mapper.JobMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 实现功能【简单定时任务调度器】
 * <p>
 * 1、采用 CAS 策略来解决多实例并发问题
 * 2、补偿机制：跳过历史补偿
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-25
 */
@Slf4j
@Component
public class JobScheduler {

    @Resource
    private JobMapper jobMapper;

    @Resource
    private JobExecuteService jobExecuteService;

    /**
     * 每隔 1 秒扫描一次到点的任务，并异步提交执行。
     */
    @Scheduled(fixedDelay = 1_000)
    public void scanAndTriggerJobs() {
        log.debug("开始扫描到点的定时任务...");

        LocalDateTime now = LocalDateTime.now();

        // 查询所有已到执行时间的启用任务
        List<JobEntity> jobList = jobMapper.selectDueJobs(now);
        if (jobList == null || jobList.isEmpty()) {
            log.debug("当前无到点需要执行的定时任务");
            return;
        }
        log.info("本次扫描到点的定时任务数量：{}", jobList.size());
        for (JobEntity job : jobList) {
            try {
                // 基于当前时间计算并更新下一次执行时间
                int row = updateNextRunTime(job, now);
                // 只有抢占成功才执行
                if (row == 1) {
                    jobExecuteService.submitAsync(job);
                }
            } catch (Exception e) {
                log.error("提交定时任务到线程池执行失败，jobId={}，jobCode={}，错误：{}", job.getId(), job.getJobCode(), e.getMessage(), e);
            }
        }
    }

    /**
     * 计算并更新任务的下一次触发时间
     */
    private int updateNextRunTime(JobEntity job, LocalDateTime now) {
        try {
            CronExpression cron = CronExpression.parse(job.getCronExpr());
            LocalDateTime next = cron.next(now);
            if (next == null) {
                log.warn("Cron 表达式无法计算下一次执行时间，停止推进任务，jobId={}，jobCode={}，cronExpr={}", job.getId(), job.getJobCode(), job.getCronExpr());
                return 0;
            }
            // 更新 next_run_time
            return jobMapper.updateNextRunTime(job.getId(), next, job.getNextRunTime());
        } catch (Exception e) {
            log.error("根据 Cron 表达式计算下一次触发时间失败，jobId={}，jobCode={}，cronExpr={}，错误：{}", job.getId(), job.getJobCode(), job.getCronExpr(), e.getMessage(), e);
        }
        return 0;
    }
}

