package com.mms.common.mq.api.constants;

/**
 * 实现功能【MQ 消息头常量】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-19 16:30:00
 */
public final class MqHeaderConstants {

    public static final String TRACE_ID = "traceId";

    public static final String EVENT_TYPE = "eventType";

    public static final String MESSAGE_KEY = "messageKey";

    private MqHeaderConstants() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}
