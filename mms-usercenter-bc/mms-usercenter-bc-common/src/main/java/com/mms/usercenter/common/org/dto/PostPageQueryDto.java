package com.mms.usercenter.common.org.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【岗位分页查询请求 DTO】
 * <p>
 * 用于分页查询岗位列表的请求参数
 * </p>
 *
 * @author li.hongyu
 * @date 2026-01-23 10:31:35
 */
@Data
@Schema(description = "岗位分页查询请求参数")
public class PostPageQueryDto {

    @Schema(description = "页码，从1开始", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "岗位名称（模糊查询）", example = "Java开发工程师")
    private String postName;

    @Schema(description = "岗位编码（模糊查询）", example = "JAVA_DEV")
    private String postCode;

    @Schema(description = "岗位等级", example = "P5")
    private String postLevel;

    @Schema(description = "状态：0-禁用，1-启用", example = "1")
    private Integer status;

    @Schema(description = "创建时间开始", example = "2026-01-01 00:00:00")
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间结束", example = "2026-12-31 23:59:59")
    private LocalDateTime createTimeEnd;
}
