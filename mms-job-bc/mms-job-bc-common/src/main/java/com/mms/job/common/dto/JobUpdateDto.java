package com.mms.job.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 实现功能【更新定时任务请求 DTO】
 * <p>
 * 用于更新定时任务定义信息的请求参数
 * </p>
 *
 * @author li.hongyu
 * @date 2026-02-11 10:30:20
 */
@Data
@Schema(description = "更新定时任务请求参数")
public class JobUpdateDto {

    @NotNull(message = "任务ID不能为空")
    @Schema(description = "任务ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "所属服务名称", example = "base")
    private String serviceName;

    @Size(max = 255, message = "任务名称长度不能超过255个字符")
    @Schema(description = "任务名称", example = "附件清理任务")
    private String jobName;

    @Schema(description = "任务类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "ATTACHMENT_CLEAN")
    private String jobType;

    @Size(max = 128, message = "Cron 表达式长度不能超过128个字符")
    @Schema(description = "Cron 表达式", example = "0 0 1 * * ?")
    private String cronExpr;

    @Size(max = 16, message = "运行模式长度不能超过16个字符")
    @Schema(description = "运行模式：single-集群只跑一份，all-每实例都跑", example = "single")
    private String runMode;

    @Schema(description = "是否启用：0-禁用，1-启用", example = "1")
    private Integer enabled;

    @Schema(description = "超时毫秒（0表示不超时）", example = "0")
    private Integer timeoutMs;

    @Schema(description = "备注", example = "生成日报并发送邮件")
    private String remark;

    @Schema(description = "任务参数JSON", example = "{\"email\":\"test@example.com\"}")
    private String paramsJson;
}

