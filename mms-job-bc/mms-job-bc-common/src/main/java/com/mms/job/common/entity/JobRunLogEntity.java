package com.mms.job.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseIdEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 实现功能【定时任务执行记录实体类】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-24 14:54:23
 */
@Data
@TableName("job_run_log")
@Schema(description = "定时任务执行记录实体")
public class JobRunLogEntity extends BaseIdEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("job_id")
    @Schema(description = "任务定义ID")
    private Long jobId;

    @TableField("job_name")
    @Schema(description = "任务名称（冗余）")
    private String jobName;

    @TableField("run_id")
    @Schema(description = "本次执行唯一ID")
    private String runId;

    @TableField("status")
    @Schema(description = "状态：RUNNING/SUCCESS/FAIL/TIMEOUT/SKIP")
    private String status;

    @TableField("start_time")
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @TableField("end_time")
    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @TableField("duration_ms")
    @Schema(description = "耗时毫秒")
    private Long durationMs;

    @TableField("instance_id")
    @Schema(description = "执行实例ID")
    private String instanceId;

    @TableField("host")
    @Schema(description = "执行机器host/IP")
    private String host;

    @TableField("error_message")
    @Schema(description = "错误摘要")
    private String errorMessage;

    @TableField("error_stack")
    @Schema(description = "错误堆栈")
    private String errorStack;

    @TableField("result_json")
    @Schema(description = "结果/统计JSON")
    private String resultJson;
}