package com.mms.common.job.web;

import com.mms.common.job.JobHandler;
import com.mms.common.job.JobHandlerRegistry;
import com.mms.common.job.dto.JobExecuteRequest;
import com.mms.common.job.dto.JobExecuteResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 实现功能【通用任务执行入口（执行器）】
 * <p>
 * 放在公共模块中，供各业务服务通过扫描自动加载。
 * <p>
 * 开关：mms.job.executor.enabled=true 时才会生效，避免默认暴露内部接口。
 *
 * @author li.hongyu
 * @date 2026-02-26 17:57:37
 */
@Slf4j
@RestController
@RequestMapping("/internal/job")
public class JobExecuteController {

    private final JobHandlerRegistry jobHandlerRegistry;

    public JobExecuteController(JobHandlerRegistry jobHandlerRegistry) {
        this.jobHandlerRegistry = jobHandlerRegistry;
    }

    /**
     * 执行任务
     * <p>
     * 调度中心调用业务服务：POST /internal/job/execute
     */
    @PostMapping("/execute")
    public JobExecuteResponse execute(@RequestBody JobExecuteRequest request) {
        if (request == null || !StringUtils.hasText(request.getJobKey())) {
            return JobExecuteResponse.fail("jobKey 不能为空");
        }

        String jobKey = request.getJobKey();
        JobHandler handler = jobHandlerRegistry.getHandler(jobKey);
        if (handler == null) {
            log.warn("未找到任务处理器，jobKey={}，requestId={}", jobKey, request.getRequestId());
            return JobExecuteResponse.fail("未找到任务处理器：" + jobKey);
        }

        long start = System.currentTimeMillis();
        try {
            log.info("开始执行任务，jobKey={}，jobId={}，requestId={}", jobKey, request.getJobId(), request.getRequestId());
            handler.execute(request.getParamsJson());
            log.info("任务执行完成，jobKey={}，耗时={}ms，requestId={}", jobKey, System.currentTimeMillis() - start, request.getRequestId());
            return JobExecuteResponse.ok();
        } catch (Exception e) {
            log.error("任务执行失败，jobKey={}，耗时={}ms，requestId={}，错误：{}",
                    jobKey, System.currentTimeMillis() - start, request.getRequestId(), e.getMessage(), e);
            return JobExecuteResponse.fail(e.getMessage());
        }
    }
}

