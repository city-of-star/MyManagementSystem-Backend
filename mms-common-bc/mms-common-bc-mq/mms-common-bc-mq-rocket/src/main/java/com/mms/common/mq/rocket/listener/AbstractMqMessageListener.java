package com.mms.common.mq.rocket.listener;

import com.mms.common.core.constants.gateway.GatewayConstants;
import com.mms.common.mq.api.exception.MqConsumeException;
import com.mms.common.mq.api.message.MqMessage;
import com.mms.common.mq.rocket.support.MqMessageSerializer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

/**
 * 实现功能【MQ 消息监听抽象基类】
 * <p>
 * 统一完成：JSON 反序列化为 {@link MqMessage}、TraceId 写入 MDC、异常日志与重试抛出。
 * 子类实现 {@link #payloadType()} 与 {@link #handleMessage(MqMessage)} 即可。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-19 18:00:00
 */
@Slf4j
public abstract class AbstractMqMessageListener<T> implements RocketMQListener<String> {

    @Resource
    private MqMessageSerializer mqMessageSerializer;

    /**
     * 业务载荷类型
     */
    protected abstract Class<T> payloadType();

    /**
     * 处理已解析的消息信封
     */
    protected abstract void handleMessage(MqMessage<T> message);

    @Override
    public void onMessage(String rawMessage) {
        MqMessage<T> message = null;
        try {
            // 反序列化 MQ 消息
            message = mqMessageSerializer.deserialize(rawMessage, payloadType());
            // 在请求里面放入 traceId，便于排查
            applyTraceId(message);
            log.info("收到 MQ 消息 eventType={}, messageKey={}", message.getEventType(), message.getMessageKey());
            // 处理消息
            handleMessage(message);
        } catch (MqConsumeException ex) {
            log.error("MQ 消息消费失败（解析/业务） eventType={}, messageKey={}", safeEventType(message), safeMessageKey(message), ex);
            throw ex;
        } catch (Exception ex) {
            log.error("MQ 消息消费失败 eventType={}, messageKey={}", safeEventType(message), safeMessageKey(message), ex);
            throw new MqConsumeException("MQ 消息消费失败", ex);
        } finally {
            MDC.remove(GatewayConstants.Mdc.TRACE_ID);
        }
    }

    /**
     * 在请求里面放入 traceId
     */
    private void applyTraceId(MqMessage<T> message) {
        if (message == null || !StringUtils.hasText(message.getTraceId())) {
            return;
        }
        MDC.put(GatewayConstants.Mdc.TRACE_ID, message.getTraceId());
    }

    /**
     * 安全获取消息的事件类型
     */
    private String safeEventType(MqMessage<T> message) {
        return message == null ? null : message.getEventType();
    }

    /**
     * 安全获取消息业务键
     */
    private String safeMessageKey(MqMessage<T> message) {
        return message == null ? null : message.getMessageKey();
    }
}
