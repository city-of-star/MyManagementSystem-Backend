package com.mms.common.job.dto;

import lombok.Data;

/**
 * 实现功能【定时任务参数验证 Dto】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-28 15:03:27
 */
@Data
public class JobValidateDto {

    /**
     * 任务类型
     */
    private String jobType;

    /**
     * 任务参数 JSON
     */
    private String paramsJson;
}