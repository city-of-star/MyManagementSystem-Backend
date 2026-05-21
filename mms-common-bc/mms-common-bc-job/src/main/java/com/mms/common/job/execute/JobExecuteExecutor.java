package com.mms.common.job.execute;

import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.job.JobHandler;
import com.mms.common.job.JobHandlerRegistry;
import com.mms.common.job.dto.JobExecuteDispatchPayload;
import com.mms.common.job.dto.JobValidateDto;
import com.mms.common.job.enums.JobTypeEnum;
import com.mms.common.job.utils.JobParamUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * 实现功能【定时任务本地执行器】
 * <p>
 * 根据 jobType 路由到 {@link JobHandler}，供 MQ 消费端与参数校验复用。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-20 10:00:00
 */
@Slf4j
@RequiredArgsConstructor
public class JobExecuteExecutor {

    private final JobHandlerRegistry jobHandlerRegistry;

    public JobExecuteOutcome execute(JobExecuteDispatchPayload payload) {
        if (payload == null || !StringUtils.hasText(payload.getJobType())) {
            return JobExecuteOutcome.fail("任务类型（jobType）不能为空", null);
        }
        String jobType = payload.getJobType();
        String jobName = JobTypeEnum.getNameByType(jobType);
        JobHandler handler = jobHandlerRegistry.getHandler(jobType);
        if (handler == null) {
            return JobExecuteOutcome.fail("未找到任务处理器：" + jobName + "（" + jobType + "）", null);
        }
        long start = System.currentTimeMillis();
        try {
            log.info("开始执行任务，jobType={}，jobId={}，requestId={}", jobType, payload.getJobId(), payload.getRequestId());
            String result = handler.execute(payload.getParamsJson());
            log.info("任务执行完成，jobType={}，耗时={}ms，requestId={}", jobType, System.currentTimeMillis() - start, payload.getRequestId());
            return JobExecuteOutcome.success(result);
        } catch (Exception e) {
            log.error("任务执行失败，jobType={}，耗时={}ms，requestId={}，错误：{}",
                    jobType, System.currentTimeMillis() - start, payload.getRequestId(), e.getMessage(), e);
            return JobExecuteOutcome.fail(e.getMessage(), stackTrace(e));
        }
    }

    /**
     * 校验任务参数 JSON 是否可解析
     */
    public String validateParams(JobValidateDto dto) {
        if (dto == null || !StringUtils.hasText(dto.getJobType())) {
            return ErrorCode.PARAM_INVALID.getMessage() + "：任务类型（jobType）不能为空";
        }
        Class<?> paramClass = jobHandlerRegistry.getParamClass(dto.getJobType());
        if (paramClass == null) {
            String jobName = JobTypeEnum.getNameByType(dto.getJobType());
            return "未找到任务处理器：" + jobName + "（" + dto.getJobType() + "）";
        }
        try {
            JobParamUtils.parseParams(dto.getParamsJson(), paramClass);
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String stackTrace(Throwable e) {
        if (e == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(e).append("\n");
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append("\tat ").append(element).append("\n");
        }
        return sb.toString();
    }
}
