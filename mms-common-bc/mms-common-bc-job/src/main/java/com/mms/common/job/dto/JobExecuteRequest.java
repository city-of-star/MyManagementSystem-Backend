package com.mms.common.job.dto;

import lombok.Data;

/**
 * 实现功能【通用任务执行请求】
 * <p>
 * 由 Job 调度中心远程调用各业务服务的“执行器入口”时使用。
 *
 * @author li.hongyu
 * @date 2026-02-26 17:57:37
 */
@Data
public class JobExecuteRequest {

    /**
     * 任务路由键（建议统一为 jobType；若你们系统以 jobCode 路由，也可以传 jobCode）
     */
    private String jobKey;

    /**
     * 任务参数 JSON（允许为空）
     */
    private String paramsJson;

    /**
     * 任务实例 ID（可选，便于链路追踪/落库）
     */
    private Long jobId;

    /**
     * 调度中心的请求标识（可选）
     */
    private String requestId;
}

