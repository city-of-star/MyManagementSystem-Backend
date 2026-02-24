package com.mms.base.common.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【定时任务信息响应 VO】
 * <p>
 * 用于返回定时任务定义信息的响应对象
 * </p>
 *
 * @author li.hongyu
 * @date 2026-02-11 10:31:00
 */
@Data
@Schema(description = "定时任务信息响应对象")
public class JobVo {

    @Schema(description = "任务ID", example = "1")
    private Long id;

    @Schema(description = "所属服务名称", example = "mms-base-bc")
    private String serviceName;

    @Schema(description = "任务编码（在所属服务下唯一）", example = "daily_report")
    private String jobCode;

    @Schema(description = "任务名称", example = "日报任务")
    private String jobName;

    @Schema(description = "Cron 表达式", example = "0 0 1 * * ?")
    private String cronExpr;

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

    @Schema(description = "创建人ID", example = "1")
    private Long createBy;

    @Schema(description = "创建时间", example = "2026-02-11 10:00:00")
    private LocalDateTime createTime;

    @Schema(description = "更新人ID", example = "1")
    private Long updateBy;

    @Schema(description = "更新时间", example = "2026-02-11 10:00:00")
    private LocalDateTime updateTime;
}

