package com.mms.base.common.system.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 实现功能【系统运行总览 VO】
 * <p>
 * 用于首页展示系统运行状态等概要信息
 * <p>
 *
 * @author li.hongyu
 * @date 2026-01-29 17:02:33
 */
@Data
@Schema(name = "SystemOverviewVo", description = "系统运行总览信息")
public class SystemOverviewVo {

    @Schema(description = "系统当前状态：UP-正常，DEGRADED-部分异常, DOWN-故障")
    private String status;

    @Schema(description = "MySQL 是否正常连接")
    private Boolean mysqlOk;

    /**
     * 为兼容旧字段，dbOk 等价于 mysqlOk
     */
    @Schema(description = "数据库是否正常连接（兼容字段，等价于 mysqlOk）")
    private Boolean dbOk;

    @Schema(description = "Redis 是否连接正常")
    private Boolean redisOk;

    @Schema(description = "在线用户数量（基于有效 Refresh Token 统计）")
    private Long onlineUsers;

    @Schema(description = "应用已运行时长（格式化文案，例如：3天4小时12分）")
    private String uptime;

    @Schema(description = "JVM 已使用内存（MB）")
    private Long jvmMemoryUsedMb;

    @Schema(description = "JVM 可用总内存（MB）")
    private Long jvmMemoryTotalMb;

    @Schema(description = "JVM 内存使用率（0-100）")
    private Double jvmMemoryUsagePercent;

    @Schema(description = "系统配置总数量")
    private Long configTotal;

    @Schema(description = "Nacos 服务器地址")
    private String nacosServerAddr;

    @Schema(description = "Nacos 命名空间")
    private String nacosNamespace;

    @Schema(description = "Nacos 分组")
    private String nacosGroup;
}
