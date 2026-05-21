package com.mms.job.core.mq.listener;

import com.mms.common.job.dto.JobExecuteResultPayload;
import com.mms.common.mq.api.constants.MqConsumerGroupNames;
import com.mms.common.mq.api.constants.MqTagConstants;
import com.mms.common.mq.api.constants.MqTopicConstants;
import com.mms.common.mq.api.message.MqMessage;
import com.mms.common.mq.rocket.annotation.MmsRocketListener;
import com.mms.common.mq.rocket.listener.AbstractMqMessageListener;
import com.mms.job.common.entity.JobRunLogEntity;
import com.mms.job.core.mapper.JobRunLogMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 实现功能【定时任务执行结果 MQ 消费者】
 * <p>
 * 接收业务服务回传的执行结果，更新 {@code job_run_log}。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-20 10:00:00
 */
@Slf4j
@Component
@ConditionalOnBean(RocketMQTemplate.class)
@MmsRocketListener(
        topic = MqTopicConstants.JOB,
        tag = MqTagConstants.JOB_EXECUTE_RESULT,
        consumerGroup = MqConsumerGroupNames.JOB_EXECUTE_RESULT
)
public class JobExecuteResultMqListener extends AbstractMqMessageListener<JobExecuteResultPayload> {

    @Resource
    private JobRunLogMapper jobRunLogMapper;

    @Override
    protected Class<JobExecuteResultPayload> payloadType() {
        return JobExecuteResultPayload.class;
    }

    @Override
    protected void handleMessage(MqMessage<JobExecuteResultPayload> message) {
        JobExecuteResultPayload payload = message.getPayload();
        if (payload == null || payload.getRunLogId() == null) {
            log.warn("执行结果消息缺少 runLogId，忽略 messageKey={}", message.getMessageKey());
            return;
        }
        if (Boolean.TRUE.equals(payload.getSuccess())) {
            markSuccess(payload);
        } else {
            markFail(payload);
        }
    }

    private void markSuccess(JobExecuteResultPayload payload) {
        JobRunLogEntity current = jobRunLogMapper.selectById(payload.getRunLogId());
        if (current == null) {
            log.warn("标记成功时未找到执行记录，runLogId={}", payload.getRunLogId());
            return;
        }
        if (!"running".equals(current.getStatus())) {
            log.info("执行记录状态为 {}，不覆盖为 success，runLogId={}", current.getStatus(), payload.getRunLogId());
            return;
        }
        JobRunLogEntity entity = new JobRunLogEntity();
        entity.setId(payload.getRunLogId());
        entity.setResultJson(payload.getResultJson());
        entity.setStatus("success");
        entity.setEndTime(LocalDateTime.now());
        if (current.getStartTime() != null) {
            entity.setDurationMs(java.time.Duration.between(current.getStartTime(), entity.getEndTime()).toMillis());
        }
        jobRunLogMapper.updateById(entity);
        log.info("定时任务执行成功，runLogId={}，jobId={}，requestId={}",
                payload.getRunLogId(), payload.getJobId(), payload.getRequestId());
    }

    private void markFail(JobExecuteResultPayload payload) {
        JobRunLogEntity current = jobRunLogMapper.selectById(payload.getRunLogId());
        if (current == null) {
            log.warn("标记失败时未找到执行记录，runLogId={}", payload.getRunLogId());
            return;
        }
        if (!"running".equals(current.getStatus())) {
            log.info("执行记录状态为 {}，不覆盖为 fail，runLogId={}", current.getStatus(), payload.getRunLogId());
            return;
        }
        JobRunLogEntity entity = new JobRunLogEntity();
        entity.setId(payload.getRunLogId());
        entity.setStatus("fail");
        entity.setEndTime(LocalDateTime.now());
        if (current.getStartTime() != null) {
            entity.setDurationMs(java.time.Duration.between(current.getStartTime(), entity.getEndTime()).toMillis());
        }
        entity.setErrorMessage(payload.getErrorMessage());
        entity.setErrorStack(payload.getErrorStack());
        jobRunLogMapper.updateById(entity);
        log.error("定时任务执行失败，runLogId={}，jobId={}，requestId={}，错误={}",
                payload.getRunLogId(), payload.getJobId(), payload.getRequestId(), payload.getErrorMessage());
    }
}
