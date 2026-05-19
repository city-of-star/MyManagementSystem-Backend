package com.mms.common.mq.api.exception;

import java.io.Serial;

/**
 * 实现功能【MQ 发送异常】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-19 16:30:00
 */
public class MqSendException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public MqSendException(String message) {
        super(message);
    }

    public MqSendException(String message, Throwable cause) {
        super(message, cause);
    }
}
