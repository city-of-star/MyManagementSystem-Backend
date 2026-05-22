package com.mms.job.core.mq.listener;

import com.mms.common.mq.api.constants.MqConsumerGroupNames;
import com.mms.common.mq.api.constants.MqTagConstants;
import com.mms.common.mq.api.constants.MqTopicConstants;
import com.mms.common.mq.api.message.MqMessage;
import com.mms.common.mq.rocket.annotation.MmsRocketListener;
import com.mms.common.mq.rocket.listener.AbstractMqMessageListener;
import com.mms.job.common.entity.JobEntity;
import com.mms.job.common.mq.JobRunMqPayload;
import com.mms.job.core.JobExecuteService;
import com.mms.job.core.mapper.JobMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 实现功能【作业触发执行 MQ 消费者】
 * <p>
 * 消费 {@link MqTagConstants#JOB_RUN_TRIGGERED} 消息并提交异步执行。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-19 18:00:00
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "mms.mq", name = "enabled", havingValue = "true")
@MmsRocketListener(
        topic = MqTopicConstants.JOB,
        tag = MqTagConstants.JOB_RUN_TRIGGERED,
        consumerGroup = MqConsumerGroupNames.JOB_RUN
)
public class JobRunTriggeredListener extends AbstractMqMessageListener<JobRunMqPayload> {

    @Resource
    private JobMapper jobMapper;

    @Resource
    private JobExecuteService jobExecuteService;

    @Override
    protected Class<JobRunMqPayload> payloadType() {
        return JobRunMqPayload.class;
    }

    @Override
    protected void handleMessage(MqMessage<JobRunMqPayload> message) {
        JobRunMqPayload payload = message.getPayload();
        if (payload == null || payload.getJobId() == null) {
            log.warn("定时任务触发消息缺少 jobId，忽略 messageKey={}", message.getMessageKey());
            return;
        }
        Long jobId = payload.getJobId();
        JobEntity job = jobMapper.selectById(jobId);
        if (job == null) {
            log.warn("定时任务不存在，跳过消费 jobId={}", jobId);
            return;
        }
        jobExecuteService.submitAsync(job);
        log.info("MQ 触发定时任务已提交异步执行 jobId={}, jobCode={}", jobId, job.getJobCode());
    }
}
