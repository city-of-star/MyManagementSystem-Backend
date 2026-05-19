package com.mms.common.mq.api.message;

import com.mms.common.mq.api.enums.MqSendStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 实现功能【MQ 发送结果】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-19 16:30:00
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MqSendResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Broker 返回的消息 ID（未实际发送时可为空）
     */
    private String messageId;

    private String topic;

    private String tag;

    private MqSendStatus status;
}
