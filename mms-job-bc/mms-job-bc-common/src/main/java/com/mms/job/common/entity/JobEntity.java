package com.mms.job.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 实现功能【定时任务实体类】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-11 10:07:31
 */
@Data
@TableName("job_def")
@Schema(description = "定时任务实体")
public class JobEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("service_name")
    @Schema(description = "所属服务")
    private String serviceName;

    @TableField("job_code")
    @Schema(description = "任务编码")
    private String jobCode;

    @TableField("job_name")
    @Schema(description = "任务名称")
    private String jobName;

    @TableField("job_type")
    @Schema(description = "任务类型")
    private String jobType;

    @TableField("cron_expr")
    @Schema(description = "Cron表达式")
    private String cronExpr;

    @TableField("next_run_time")
    @Schema(description = "下一次触发时间")
    private LocalDateTime nextRunTime;

    @TableField("run_mode")
    @Schema(description = "运行模式：single-集群只跑一份，all-每实例都跑")
    private String runMode;

    @TableField("enabled")
    @Schema(description = "是否启用：0-禁用，1-启用")
    private Integer enabled;

    @TableField("timeout_ms")
    @Schema(description = "超时毫秒（0表示不超时）")
    private Integer timeoutMs;

    @TableField("remark")
    @Schema(description = "备注")
    private String remark;

    @TableField("params_json")
    @Schema(description = "任务参数JSON")
    private String paramsJson;
}