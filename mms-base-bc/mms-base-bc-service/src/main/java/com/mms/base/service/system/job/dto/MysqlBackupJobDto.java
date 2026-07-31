package com.mms.base.service.system.job.dto;

import lombok.Data;

/**
 * 实现功能【MySQL 备份任务参数 DTO】
 * <p>
 * 主要配置走 Nacos mms.backup.*；此处仅提供可选覆盖项。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Data
public class MysqlBackupJobDto {

    /**
     * 覆盖保留天数；null 则用 Nacos 配置
     */
    private Integer retainDays;

    /**
     * 覆盖库名；null 则用 Nacos / 数据源解析
     */
    private String database;
}
