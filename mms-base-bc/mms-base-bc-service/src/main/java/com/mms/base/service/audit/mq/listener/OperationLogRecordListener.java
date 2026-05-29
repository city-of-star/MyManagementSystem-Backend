package com.mms.base.service.audit.mq.listener;

import com.mms.base.common.audit.entity.OperationLogEntity;
import com.mms.base.service.audit.mapper.OperationLogMapper;
import com.mms.common.mq.api.constants.MqConsumerGroupNames;
import com.mms.common.mq.api.constants.MqTagConstants;
import com.mms.common.mq.api.constants.MqTopicConstants;
import com.mms.common.mq.api.message.MqMessage;
import com.mms.common.mq.rocket.annotation.MmsRocketListener;
import com.mms.common.mq.rocket.listener.AbstractMqMessageListener;
import com.mms.common.webmvc.audit.OperationLogRecordMqPayload;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.util.StringUtils;

/**
 * 实现功能【操作日志 MQ 消费者】
 * <p>
 * TODO: audit_operation_log 定时清理（建议保留 180 天，可配置）。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-29 14:00:00
 */
@Slf4j
@MmsRocketListener(
        topic = MqTopicConstants.BASE,
        tag = MqTagConstants.AUDIT_OPERATION_LOG_RECORD,
        consumerGroup = MqConsumerGroupNames.AUDIT_OPERATION_LOG
)
public class OperationLogRecordListener extends AbstractMqMessageListener<OperationLogRecordMqPayload> {

    @Resource
    private OperationLogMapper operationLogMapper;

    @Override
    protected Class<OperationLogRecordMqPayload> payloadType() {
        return OperationLogRecordMqPayload.class;
    }

    @Override
    protected void handleMessage(MqMessage<OperationLogRecordMqPayload> message) {
        OperationLogRecordMqPayload payload = message.getPayload();
        if (payload == null || payload.getId() == null) {
            log.warn("操作日志 MQ 载荷无效，忽略");
            return;
        }
        try {
            operationLogMapper.insert(toEntity(payload));
        } catch (DuplicateKeyException ex) {
            log.info("重复的操作日志消息，已忽略, logId={}", payload.getId());
        }
    }

    private OperationLogEntity toEntity(OperationLogRecordMqPayload payload) {
        OperationLogEntity entity = new OperationLogEntity();
        entity.setId(payload.getId());
        entity.setTraceId(payload.getTraceId());
        entity.setUserId(payload.getUserId());
        entity.setUsername(payload.getUsername());
        entity.setModule(payload.getModule());
        entity.setOperationType(payload.getOperationType());
        entity.setOperationDesc(payload.getOperationDesc());
        entity.setRequestMethod(payload.getRequestMethod());
        entity.setRequestUrl(payload.getRequestUrl());
        entity.setRequestIp(payload.getRequestIp());
        entity.setRequestParams(payload.getRequestParams());
        entity.setResponseData(payload.getResponseData());
        entity.setOperationStatus(payload.getOperationStatus());
        entity.setErrorMessage(payload.getErrorMessage());
        entity.setCostMs(payload.getCostMs());
        entity.setOperationTime(payload.getOperationTime());
        if (!StringUtils.hasText(entity.getUsername()) && entity.getUserId() == null) {
            log.warn("操作日志缺少用户信息, logId={}", payload.getId());
        }
        return entity;
    }
}
