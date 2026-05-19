package com.mms.common.mq.api.enums;

/**
 * 实现功能【MQ 发送状态枚举】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-19 16:30:00
 */
public enum MqSendStatus {

    /**
     * 已成功投递至 Broker
     */
    SUCCESS,

    /**
     * 未启用 MQ，跳过发送
     */
    SKIPPED,

    /**
     * 发送失败
     */
    FAILED
}
