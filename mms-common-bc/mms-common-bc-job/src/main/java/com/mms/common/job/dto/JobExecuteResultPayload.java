package com.mms.common.job.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 实现功能【定时任务执行结果 MQ 载荷】
 * <p>
 * 业务服务执行完成后回传 job 调度中心。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-20 10:00:00
 */
@Data
public class JobExecuteResultPayload implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long runLogId;

    private String requestId;

    private Long jobId;

    private Boolean success;

    private String resultJson;

    private String errorMessage;

    private String errorStack;
}
