package com.mms.common.mq.api.constants;

/**
 * 实现功能【MQ Tag 命名常量】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-19 16:30:00
 */
public final class MqTagConstants {

    /**
     * 连通性探测
     */
    public static final String PING = "ping";

    /**
     * 定时任务触发执行
     */
    public static final String JOB_RUN_TRIGGERED = "job_run_triggered";

    private MqTagConstants() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}
