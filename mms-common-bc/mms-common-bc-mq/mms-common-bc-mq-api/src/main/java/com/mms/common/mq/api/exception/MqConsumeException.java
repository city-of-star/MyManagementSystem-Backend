package com.mms.common.mq.api.exception;

import java.io.Serial;

/**
 * 实现功能【MQ 消费异常】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-19 18:00:00
 */
public class MqConsumeException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public MqConsumeException(String message) {
        super(message);
    }

    public MqConsumeException(String message, Throwable cause) {
        super(message, cause);
    }
}
