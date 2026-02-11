package com.mms.common.threadpool.properties;

import com.mms.common.threadpool.enums.RejectedPolicy;
import lombok.Data;

/**
 * 实现功能【线程池执行器通用配置项】
 * <p>
 * 
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-11 17:18:19
 */
@Data
public class ExecutorProperties {

    /**
     * 核心线程数
     */
    private int corePoolSize;

    /**
     * 最大线程数
     */
    private int maxPoolSize;

    /**
     * 队列容量
     */
    private int queueCapacity;

    /**
     * 空闲线程存活时间（单位：秒）
     */
    private long keepAliveTime;

    /**
     * 是否允许核心线程超时回收
     */
    private boolean allowCoreThreadTimeOut = false;

    /**
     * 线程名前缀（可选，不配则按用途自动生成）
     */
    private String threadNamePrefix;

    /**
     * 拒绝策略
     */
    private RejectedPolicy rejectedPolicy = RejectedPolicy.CALLER_RUNS;

    /**
     * 默认的定时任务线程池配置
     */
    public static ExecutorProperties schedulerDefaults() {
        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorProperties p = new ExecutorProperties();
        p.setCorePoolSize(cores);
        p.setMaxPoolSize(cores * 2);
        p.setQueueCapacity(1000);
        p.setKeepAliveTime(60L);
        p.setAllowCoreThreadTimeOut(false);
        p.setRejectedPolicy(RejectedPolicy.CALLER_RUNS);
        return p;
    }

    /**
     * 默认的文件上传/下载线程池配置
     */
    public static ExecutorProperties fileDefaults() {
        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorProperties p = new ExecutorProperties();
        p.setCorePoolSize(cores * 2);
        p.setMaxPoolSize(cores * 4);
        p.setQueueCapacity(2000);
        p.setKeepAliveTime(120L);
        p.setAllowCoreThreadTimeOut(true);
        p.setRejectedPolicy(RejectedPolicy.CALLER_RUNS);
        return p;
    }
}