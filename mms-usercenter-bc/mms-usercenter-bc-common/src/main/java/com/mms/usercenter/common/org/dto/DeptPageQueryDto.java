package com.mms.usercenter.common.org.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【部门分页查询请求 DTO】
 * <p>
 * 用于分页查询部门列表的请求参数
 * </p>
 *
 * @author li.hongyu
 * @date 2026-01-23 10:30:15
 */
@Data
@Schema(description = "部门分页查询请求参数")
public class DeptPageQueryDto {

    @Schema(description = "页码，从1开始", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "部门名称（模糊查询）", example = "技术部")
    private String deptName;

    @Schema(description = "部门编码（模糊查询）", example = "TECH")
    private String deptCode;

    @Schema(description = "父部门ID", example = "0")
    private Long parentId;

    @Schema(description = "状态：0-禁用，1-启用", example = "1")
    private Integer status;

    @Schema(description = "创建时间开始", example = "2026-01-01 00:00:00")
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间结束", example = "2026-12-31 23:59:59")
    private LocalDateTime createTimeEnd;
}
