package com.mms.job.core;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mms.job.common.entity.JobEntity;
import com.mms.job.core.mapper.JobMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
     * 简单调度：每隔 30 秒扫描一次启用中的任务，并异步提交执行。
     */
    @Scheduled(fixedDelay = 30_000)
    public void scanAndTriggerJobs() {
        log.info("开始扫描启用中的定时任务...");

        // 查询所有启用的任务
        LambdaQueryWrapper<JobEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(JobEntity::getEnabled, 1);
        List<JobEntity> jobList = jobMapper.selectList(wrapper);
        if (jobList == null || jobList.isEmpty()) {
            log.info("当前无启用中的定时任务");
            return;
        }
        log.info("本次扫描到启用中的定时任务数量：{}", jobList.size());
        for (JobEntity job : jobList) {
            try {
                jobExecuteService.submitAsync(job);
            } catch (Exception e) {
                log.error("提交定时任务到线程池执行失败，jobId={}，jobCode={}，错误：{}", job.getId(), job.getJobCode(), e.getMessage(), e);
            }
        }
    }
}

