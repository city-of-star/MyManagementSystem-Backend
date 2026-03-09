package com.mms.base.common.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;

/**
 * 实现功能【系统配置分页查询请求 DTO】
 * <p>
 * 用于分页查询系统配置列表的请求参数
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-23 11:21:50
 */
@Data
@Schema(description = "系统配置分页查询请求参数")
public class ConfigPageQueryDto {

    @Schema(description = "页码，从1开始", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "配置键（模糊查询）", example = "system")
    private String configKey;

    @Schema(description = "配置名称（模糊查询）", example = "系统")
    private String configName;

    @Schema(description = "配置类型", example = "string")
    private String configType;

    @Range(min = 0, max = 1, message = "状态值只能是0或1")
    @Schema(description = "状态：0-禁用，1-启用", example = "1")
    private Integer status;

    @Range(min = 0, max = 1, message = "编辑值值只能是0或1")
    @Schema(description = "是否可编辑：0-否（系统配置），1-是（用户配置）", example = "1")
    private Integer editable;

    @Schema(description = "创建时间开始", example = "2025-01-01 00:00:00")
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间结束", example = "2025-12-31 23:59:59")
    private LocalDateTime createTimeEnd;
}

