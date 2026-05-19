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
     * 作业触发执行消费组
     */
    public static final String JOB_RUN = "${spring.application.name}_job_run_consumer";

    private MqConsumerGroupNames() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}
