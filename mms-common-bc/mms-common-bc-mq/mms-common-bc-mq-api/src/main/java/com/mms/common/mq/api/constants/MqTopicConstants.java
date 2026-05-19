package com.mms.common.mq.api.constants;

/**
 * 实现功能【MQ Topic 命名常量】
 * <p>
 * 命名约定：{bc}_{domain}，全小写下划线。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-19 16:30:00
 */
public final class MqTopicConstants {

    /**
     * 作业域 Topic
     */
    public static final String JOB = "mms_job";

    /**
     * 用户中心域 Topic
     */
    public static final String USERCENTER = "mms_usercenter";

    /**
     * 基础数据域 Topic
     */
    public static final String BASE = "mms_base";

    private MqTopicConstants() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}
