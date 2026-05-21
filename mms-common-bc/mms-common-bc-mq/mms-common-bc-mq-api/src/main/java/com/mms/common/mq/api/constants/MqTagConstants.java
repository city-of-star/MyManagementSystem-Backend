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
     * 作业调度中心内部触发（仅 job 服务）
     */
    public static final String JOB_RUN_TRIGGERED = "job_run_triggered";

    /**
     * 定时任务执行指令（Tag 为业务服务名，如 base、usercenter）
     */
    public static final String JOB_EXECUTE = "job_execute";

    /**
     * 定时任务执行结果回传（仅 job 服务消费）
     */
    public static final String JOB_EXECUTE_RESULT = "job_execute_result";

    private MqTagConstants() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}
