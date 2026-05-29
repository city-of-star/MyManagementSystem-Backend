package com.mms.common.webmvc.audit;

import com.mms.common.mq.api.constants.MqEventTypeConstants;
import com.mms.common.mq.api.constants.MqTagConstants;
import com.mms.common.mq.api.constants.MqTopicConstants;
import com.mms.common.mq.api.message.MqMessage;
import com.mms.common.mq.api.service.MqSendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 实现功能【操作日志 MQ 发布器】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-05-29 14:00:00
 */
@Slf4j
@RequiredArgsConstructor
public class OperationLogPublisher {

    private final MqSendService mqSendService;

    /**
     * 发布操作日志记录消息
     */
    public void publish(OperationLogRecordMqPayload payload) {
        if (payload == null || payload.getId() == null) {
            return;
        }
        try {
            MqMessage<OperationLogRecordMqPayload> message = MqMessage.<OperationLogRecordMqPayload>builder()
                    .messageKey(String.valueOf(payload.getId()))
                    .eventType(MqEventTypeConstants.AUDIT_OPERATION_LOG_RECORD)
                    .payload(payload)
                    .build();
            mqSendService.send(MqTopicConstants.BASE, MqTagConstants.AUDIT_OPERATION_LOG_RECORD, message);
        } catch (Exception ex) {
            log.error("操作日志 MQ 发送失败, logId={}", payload.getId(), ex);
        }
    }
}
