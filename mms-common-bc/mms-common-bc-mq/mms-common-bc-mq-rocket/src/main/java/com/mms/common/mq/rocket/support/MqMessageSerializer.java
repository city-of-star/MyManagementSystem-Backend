package com.mms.common.mq.rocket.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mms.common.core.utils.JacksonObjectMapperUtils;
import com.mms.common.mq.api.exception.MqConsumeException;
import com.mms.common.mq.api.exception.MqSendException;
import com.mms.common.mq.api.message.MqMessage;

/**
 * 实现功能【MQ 消息 JSON 序列化】
 * <p>
 * 与项目统一的 Jackson 时间格式保持一致。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-19 16:30:00
 */
public class MqMessageSerializer {

    private final ObjectMapper objectMapper = JacksonObjectMapperUtils.createCommonObjectMapper();

    /**
     * 序列化 MQ 消息
     */
    public String serialize(MqMessage<?> message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException ex) {
            throw new MqSendException("MQ 消息序列化失败", ex);
        }
    }

    /**
     * 反序列化 MQ 消息
     */
    public <T> MqMessage<T> deserialize(String json, Class<T> payloadType) {
        try {
            return objectMapper.readValue(
                    json,
                    objectMapper.getTypeFactory().constructParametricType(MqMessage.class, payloadType)
            );
        } catch (JsonProcessingException ex) {
            throw new MqConsumeException("MQ 消息反序列化失败", ex);
        }
    }
}
