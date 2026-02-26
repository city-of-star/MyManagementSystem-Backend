package com.mms.job.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 实现功能【创建定时任务请求 DTO】
 * <p>
 * 用于创建新定时任务定义的请求参数
 * </p>
 *
 * @author li.hongyu
 * @date 2026-02-11 10:30:10
 */
@Data
@Schema(description = "创建定时任务请求参数")
public class JobCreateDto {

    @NotBlank(message = "所属服务不能为空")
    @Schema(description = "所属服务名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "base")
    private String serviceName;

    @NotBlank(message = "任务编码不能为空")
    @Size(max = 128, message = "任务编码长度不能超过128个字符")
    @Schema(description = "任务编码（在所属服务下唯一）", requiredMode = Schema.RequiredMode.REQUIRED, example = "ATTACHMENT_CLEAN")
    private String jobCode;

    @NotBlank(message = "任务名称不能为空")
    @Size(max = 255, message = "任务名称长度不能超过255个字符")
    @Schema(description = "任务名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "附件清理任务")
    private String jobName;

    @NotBlank(message = "任务类型不能为空")
    @Schema(description = "任务类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "ATTACHMENT_CLEAN")
    private String jobType;

    @NotBlank(message = "Cron 表达式不能为空")
    @Size(max = 128, message = "Cron 表达式长度不能超过128个字符")
    @Schema(description = "Cron 表达式", requiredMode = Schema.RequiredMode.REQUIRED, example = "0 0 2 * * ?")
    private String cronExpr;

    @NotBlank(message = "运行模式不能为空")
    @Size(max = 16, message = "运行模式长度不能超过16个字符")
    @Schema(description = "运行模式：single-集群只跑一份，all-每实例都跑", requiredMode = Schema.RequiredMode.REQUIRED, example = "single")
    private String runMode;

    @Schema(description = "是否启用：0-禁用，1-启用，默认为1", example = "1")
    private Integer enabled = 1;

    @Schema(description = "超时毫秒（0表示不超时），默认为0", example = "0")
    private Integer timeoutMs = 0;

    @Schema(description = "备注", example = "附件清理任务")
    private String remark;

    @Schema(description = "任务参数JSON", example = "{\"batchSize\":100}")
    private String paramsJson;
}

