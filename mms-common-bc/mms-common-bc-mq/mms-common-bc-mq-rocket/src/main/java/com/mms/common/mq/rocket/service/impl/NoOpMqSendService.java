package com.mms.common.mq.rocket.service.impl;

import com.mms.common.mq.api.enums.MqSendStatus;
import com.mms.common.mq.api.message.MqMessage;
import com.mms.common.mq.api.message.MqSendResult;
import com.mms.common.mq.api.service.MqSendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * 实现功能【MQ 空实现发送服务】
 * <p>
 * 在未启用 {@code mms.mq.enabled} 时注入，避免本地无 Broker 时启动失败。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-19 16:30:00
 */
@Slf4j
public class NoOpMqSendService implements MqSendService {

    @Override
    public MqSendResult send(String topic, String tag, MqMessage<?> message) {
        if (!StringUtils.hasText(topic) || !StringUtils.hasText(tag) || message == null) {
            log.warn("MQ 未启用且参数不完整，跳过发送 topic={}, tag={}", topic, tag);
        } else {
            log.debug("MQ 未启用，跳过发送 topic={}, tag={}, eventType={}", topic, tag, message.getEventType());
        }
        return MqSendResult.builder()
                .topic(topic)
                .tag(tag)
                .status(MqSendStatus.SKIPPED)
                .build();
    }
}
