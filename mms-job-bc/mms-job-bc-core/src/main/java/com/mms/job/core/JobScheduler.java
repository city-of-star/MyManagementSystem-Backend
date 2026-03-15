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
 *
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
                // 先基于当前时间计算并更新下一次执行时间，避免并发多次触发
                updateNextRunTime(job, now);
                // 再提交执行
                jobExecuteService.submitAsync(job);
            } catch (Exception e) {
                log.error("提交定时任务到线程池执行失败，jobId={}，jobCode={}，错误：{}", job.getId(), job.getJobCode(), e.getMessage(), e);
            }
        }
    }

    /**
     * 计算并更新任务的下一次触发时间
     */
    private void updateNextRunTime(JobEntity job, LocalDateTime baseTime) {
        try {
            CronExpression cron = CronExpression.parse(job.getCronExpr());
            LocalDateTime next = cron.next(baseTime);
            // 更新 next_run_time
            jobMapper.updateNextRunTime(job.getId(), next);
        } catch (Exception e) {
            log.error("根据 Cron 表达式计算下一次触发时间失败，jobId={}，jobCode={}，cronExpr={}，错误：{}", job.getId(), job.getJobCode(), job.getCronExpr(), e.getMessage(), e);
        }
    }
}

