package com.mms.common.mq.api.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 实现功能【MQ 统一消息信封】
 * <p>
 * 业务载荷与链路元数据分离，便于跨服务序列化与消费端解析。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-19 16:30:00
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MqMessage<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 消息业务键
     */
    private String messageKey;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 链路追踪 ID
     */
    private String traceId;

    /**
     * 事件发生时间
     */
    private LocalDateTime occurredAt;

    /**
     * 扩展头
     */
    @Builder.Default
    private Map<String, String> headers = new HashMap<>();

    /**
     * 业务载荷
     */
    private T payload;
}
