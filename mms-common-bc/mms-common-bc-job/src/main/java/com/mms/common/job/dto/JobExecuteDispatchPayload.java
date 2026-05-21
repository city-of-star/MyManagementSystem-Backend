package com.mms.common.job.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 实现功能【定时任务执行 MQ 调度载荷】
 * <p>
 * 由 job 调度中心投递，各业务服务按 Tag（服务名）消费并执行。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-20 10:00:00
 */
@Data
public class JobExecuteDispatchPayload implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long jobId;

    private String jobType;

    private String paramsJson;

    private String requestId;

    /**
     * job-bc 运行日志主键，用于结果回传更新状态
     */
    private Long runLogId;
}
