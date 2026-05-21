package com.mms.common.job.mq;

import com.mms.common.mq.api.constants.MqConsumerGroupNames;
import com.mms.common.mq.api.constants.MqTagConstants;
import com.mms.common.mq.api.constants.MqTopicConstants;
import com.mms.common.mq.api.message.MqMessage;
import com.mms.common.mq.api.service.MqSendService;
import com.mms.common.mq.rocket.annotation.MmsRocketListener;
import com.mms.common.mq.rocket.listener.AbstractMqMessageListener;
import com.mms.common.job.dto.JobExecuteDispatchPayload;
import com.mms.common.job.dto.JobExecuteResultPayload;
import com.mms.common.job.execute.JobExecuteExecutor;
import com.mms.common.job.execute.JobExecuteOutcome;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * 实现功能【定时任务执行 MQ 消费者】
 * <p>
 * 各业务服务按 {@code spring.application.name} 作为 Tag 订阅执行指令，执行后回传结果至 job 服务。
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
        tag = "${spring.application.name}",
        consumerGroup = MqConsumerGroupNames.JOB_EXECUTE
)
public class JobExecuteMqListener extends AbstractMqMessageListener<JobExecuteDispatchPayload> {

    @Resource
    private JobExecuteExecutor jobExecuteExecutor;

    @Resource
    private MqSendService mqSendService;

    @Override
    protected Class<JobExecuteDispatchPayload> payloadType() {
        return JobExecuteDispatchPayload.class;
    }

    @Override
    protected void handleMessage(MqMessage<JobExecuteDispatchPayload> message) {
        JobExecuteDispatchPayload payload = message.getPayload();
        if (payload == null) {
            log.warn("定时任务执行消息载荷为空，忽略 messageKey={}", message.getMessageKey());
            return;
        }
        JobExecuteOutcome outcome = jobExecuteExecutor.execute(payload);
        sendResult(payload, outcome);
    }

    private void sendResult(JobExecuteDispatchPayload dispatch, JobExecuteOutcome outcome) {
        JobExecuteResultPayload result = new JobExecuteResultPayload();
        result.setRunLogId(dispatch.getRunLogId());
        result.setRequestId(dispatch.getRequestId());
        result.setJobId(dispatch.getJobId());
        result.setSuccess(outcome.isSuccess());
        result.setResultJson(outcome.getResultJson());
        result.setErrorMessage(outcome.getErrorMessage());
        result.setErrorStack(outcome.getErrorStack());

        MqMessage<JobExecuteResultPayload> resultMessage = MqMessage.<JobExecuteResultPayload>builder()
                .eventType(MqTagConstants.JOB_EXECUTE_RESULT)
                .messageKey(dispatch.getRequestId())
                .payload(result)
                .build();
        mqSendService.send(MqTopicConstants.JOB, MqTagConstants.JOB_EXECUTE_RESULT, resultMessage);
        log.info("定时任务执行结果已回传 jobId={}, requestId={}, success={}",
                dispatch.getJobId(), dispatch.getRequestId(), outcome.isSuccess());
    }
}
