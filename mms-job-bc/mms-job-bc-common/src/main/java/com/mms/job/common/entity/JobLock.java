package com.mms.job.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseIdEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 实现功能【定时任务执行锁实体类】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-24 14:54:37
 */
@Data
@TableName("job_lock")
@Schema(description = "定时任务执行锁实体")
public class JobLock extends BaseIdEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("job_id")
    @Schema(description = "任务定义ID")
    private Long jobId;

    @TableField("instance_id")
    @Schema(description = "持有锁的实例ID")
    private String instanceId;

    @TableField("lock_time")
    @Schema(description = "锁定时间")
    private LocalDateTime lockTime;

    @TableField("expire_time")
    @Schema(description = "锁过期时间")
    private LocalDateTime expireTime;

    @TableField("heartbeat_time")
    @Schema(description = "心跳时间（用于续期）")
    private LocalDateTime heartbeatTime;
}