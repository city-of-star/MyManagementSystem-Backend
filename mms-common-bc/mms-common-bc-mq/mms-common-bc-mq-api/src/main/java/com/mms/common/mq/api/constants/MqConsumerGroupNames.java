package com.mms.common.mq.api.constants;

/**
 * 实现功能【MQ ConsumerGroup 命名常量（SpEL）】
 * <p>
 * 命名约定：{@code ${spring.application.name}_{suffix}_consumer}
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-19 18:00:00
 */
public final class MqConsumerGroupNames {

    /**
     * 作业触发执行消费组（job 服务）
     */
    public static final String JOB_RUN = "${spring.application.name}_job_run_consumer";

    /**
     * 定时任务执行消费组（各业务服务，Tag 为服务名）
     */
    public static final String JOB_EXECUTE = "${spring.application.name}_job_execute_consumer";

    /**
     * 定时任务执行结果消费组（job 服务）
     */
    public static final String JOB_EXECUTE_RESULT = "${spring.application.name}_job_execute_result_consumer";

    private MqConsumerGroupNames() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}
