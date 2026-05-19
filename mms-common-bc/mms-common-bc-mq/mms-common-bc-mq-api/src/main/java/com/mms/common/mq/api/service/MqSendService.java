package com.mms.common.mq.api.service;

import com.mms.common.mq.api.message.MqMessage;
import com.mms.common.mq.api.message.MqSendResult;

/**
 * 实现功能【MQ 发送服务契约】
 * <p>
 * 业务模块仅依赖本接口，与具体 Broker 实现解耦。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-19 16:30:00
 */
public interface MqSendService {

    /**
     * 同步发送消息
     *
     * @param topic   Topic，不可为空
     * @param tag     Tag，不可为空
     * @param message 消息信封，不可为空
     * @return 发送结果
     */
    MqSendResult send(String topic, String tag, MqMessage<?> message);
}
