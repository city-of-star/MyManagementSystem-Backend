package com.mms.common.mq.rocket.service.impl;

import com.mms.common.core.constants.gateway.GatewayConstants;
import com.mms.common.core.utils.DateUtils;
import com.mms.common.core.utils.IdUtils;
import com.mms.common.mq.api.constants.MqHeaderConstants;
import com.mms.common.mq.api.enums.MqSendStatus;
import com.mms.common.mq.api.exception.MqSendException;
import com.mms.common.mq.api.message.MqMessage;
import com.mms.common.mq.api.message.MqSendResult;
import com.mms.common.mq.api.service.MqSendService;
import com.mms.common.mq.rocket.support.MqMessageSerializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.MDC;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.StringUtils;

import java.util.HashMap;

/**
 * 实现功能【RocketMQ 同步发送实现】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-19 16:30:00
 */
@Slf4j
@RequiredArgsConstructor
public class RocketMqSendService implements MqSendService {

    private final RocketMQTemplate rocketMQTemplate;

    private final MqMessageSerializer mqMessageSerializer;

    @Override
    public MqSendResult send(String topic, String tag, MqMessage<?> message) {
        // 校验参数
        validate(topic, tag, message);
        // 消息补全
        enrich(message);
        // 构造发送地址
        String destination = topic + ":" + tag;
        // 序列化消息
        String body = mqMessageSerializer.serialize(message);
        // 创建消息对象
        Message<String> springMessage = MessageBuilder.withPayload(body)
                .setHeader(MqHeaderConstants.MESSAGE_KEY, message.getMessageKey())
                .setHeader(MqHeaderConstants.TRACE_ID, message.getTraceId())
                .setHeader(MqHeaderConstants.EVENT_TYPE, message.getEventType())
                .build();
        try {
            // 发送消息
            SendResult sendResult = rocketMQTemplate.syncSend(destination, springMessage);
            log.info("MQ 消息发送成功 topic={}, tag={}, messageKey={}, msgId={}", topic, tag, message.getMessageKey(), sendResult.getMsgId());
            return MqSendResult.builder()
                    .messageId(sendResult.getMsgId())
                    .topic(topic)
                    .tag(tag)
                    .status(MqSendStatus.SUCCESS)
                    .build();
        } catch (Exception ex) {
            log.error("MQ 消息发送失败 topic={}, tag={}, messageKey={}", topic, tag, message.getMessageKey(), ex);
            throw new MqSendException("MQ 消息发送失败: " + topic + ":" + tag, ex);
        }
    }

    /**
     * 发送前消息信封补全
     */
    private void enrich(MqMessage<?> message) {
        if (message == null) {
            return;
        }
        // 设置消息业务键
        if (!StringUtils.hasText(message.getMessageKey())) {
            message.setMessageKey(IdUtils.uuid32());
        }
        // 设置 traceId
        if (!StringUtils.hasText(message.getTraceId())) {
            message.setTraceId(MDC.get(GatewayConstants.Mdc.TRACE_ID));
        }
        // 设置扩展头
        if (message.getHeaders() == null) {
            message.setHeaders(new HashMap<>());
        }
        // 设置事件发生时间
        if (message.getOccurredAt() == null) {
            message.setOccurredAt(DateUtils.now());
        }
    }

    /**
     * 校验参数是否合法
     */
    private void validate(String topic, String tag, MqMessage<?> message) {
        if (!StringUtils.hasText(topic)) {
            throw new MqSendException("MQ topic 不能为空");
        }
        if (!StringUtils.hasText(tag)) {
            throw new MqSendException("MQ tag 不能为空");
        }
        if (message == null) {
            throw new MqSendException("MQ 消息不能为空");
        }
    }
}
