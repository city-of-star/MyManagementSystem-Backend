package com.mms.common.job.dto;

import lombok.Data;

/**
 * 实现功能【通用任务执行请求】
 * <p>
 *
 * <p>
 * @author li.hongyu
 * @date 2026-02-26 17:57:37
 */
@Data
public class JobExecuteDto {

    /**
     * 任务类型
     */
    private String jobType;

    /**
     * 任务参数 JSON（允许为空）
     */
    private String paramsJson;

    /**
     * 任务实例 ID
     */
    private Long jobId;

    /**
     * 调度中心的请求标识
     */
    private String requestId;
}

