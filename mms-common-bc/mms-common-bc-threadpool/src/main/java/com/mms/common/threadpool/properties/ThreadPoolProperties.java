package com.mms.common.threadpool.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 实现功能【线程池配置属性】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-11 16:36:39
 */
@Data
@ConfigurationProperties(prefix = "threadpool")
public class ThreadPoolProperties {

    /**
     * 定时任务线程池配置
     */
    private ExecutorProperties scheduler = ExecutorProperties.schedulerDefaults();

    /**
     * 文件上传/下载线程池配置
     */
    private ExecutorProperties file = ExecutorProperties.fileDefaults();
}

