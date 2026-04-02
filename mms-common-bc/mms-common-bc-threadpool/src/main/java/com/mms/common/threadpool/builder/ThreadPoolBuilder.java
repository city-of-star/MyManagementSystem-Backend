package com.mms.common.threadpool.builder;

import com.mms.common.threadpool.enums.RejectedPolicy;
import com.mms.common.threadpool.properties.ExecutorProperties;
import com.mms.common.threadpool.properties.ThreadPoolProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 实现功能【线程池构建器】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-03-03 11:31:35
 */
@Slf4j
public class ThreadPoolBuilder {

    /**
     * 定时任务线程池
     */
    public ThreadPoolTaskExecutor schedulerTaskExecutor(ThreadPoolProperties properties) {
        ExecutorProperties cfg = properties.getScheduler();
        String prefix = cfg.getThreadNamePrefix() != null ? cfg.getThreadNamePrefix() : "scheduler-";
        ThreadPoolTaskExecutor executor = buildExecutor(cfg, prefix);
        log.info("【定时任务线程池】加载成功：core={}, max={}, queue={}, prefix={}", cfg.getCorePoolSize(), cfg.getMaxPoolSize(), cfg.getQueueCapacity(), prefix);
        return executor;
    }

    /**
     * 文件上传/下载线程池
     */
    public ThreadPoolTaskExecutor fileTaskExecutor(ThreadPoolProperties properties) {
        ExecutorProperties cfg = properties.getFile();
        String prefix = cfg.getThreadNamePrefix() != null ? cfg.getThreadNamePrefix() : "file-";
        ThreadPoolTaskExecutor executor = buildExecutor(cfg, prefix);
        log.info("【文件线程池】加载成功：core={}, max={}, queue={}, prefix={}", cfg.getCorePoolSize(), cfg.getMaxPoolSize(), cfg.getQueueCapacity(), prefix);
        return executor;
    }

    /**
     * 构建线程池
     */
    public ThreadPoolTaskExecutor buildExecutor(ExecutorProperties cfg, String threadNamePrefix) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(cfg.getCorePoolSize());
        // 设置最大线程数
        executor.setMaxPoolSize(cfg.getMaxPoolSize());
        // 设置任务队列长度
        executor.setQueueCapacity(cfg.getQueueCapacity());
        // 设置空闲线程存活时间（秒）
        executor.setKeepAliveSeconds((int) cfg.getKeepAliveTime());
        // 是否允许核心线程在空闲超时后被回收
        executor.setAllowCoreThreadTimeOut(cfg.isAllowCoreThreadTimeOut());
        // 设置线程名前缀
        executor.setThreadNamePrefix(threadNamePrefix);
        // 设置拒绝策略
        executor.setRejectedExecutionHandler(toJdkPolicy(cfg.getRejectedPolicy()));
        // 设置应用关闭时等待任务完成
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 设置应用关闭时等待任务完成时间（秒）
        executor.setAwaitTerminationSeconds(30);
        // 初始化线程池
        executor.initialize();
        return executor;
    }

    /**
     * 将拒绝策略转换为 JDK 内置策略
     */
    public RejectedExecutionHandler toJdkPolicy(RejectedPolicy policy) {
        if (policy == null) {
            return new ThreadPoolExecutor.CallerRunsPolicy();
        }
        return switch (policy) {
            case CALLER_RUNS -> new ThreadPoolExecutor.CallerRunsPolicy();
            case ABORT -> new ThreadPoolExecutor.AbortPolicy();
            case DISCARD -> new ThreadPoolExecutor.DiscardPolicy();
            case DISCARD_OLDEST -> new ThreadPoolExecutor.DiscardOldestPolicy();
        };
    }
}