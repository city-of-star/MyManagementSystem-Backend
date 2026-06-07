package com.mms.common.mq.api.constants;

/**
 * 实现功能【MQ 事件类型命名常量】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-29 14:00:00
 */
public final class MqEventTypeConstants {

    /**
     * 操作日志记录
     */
    public static final String AUDIT_OPERATION_LOG_RECORD = "audit_operation_log_record";

    private MqEventTypeConstants() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}
