package com.mms.usercenter.common.org.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 实现功能【部门树查询 DTO】
 * <p>
 * 用于查询全量部门树的过滤条件（非分页）
 * </p>
 *
 * @author li.hongyu
 * @date 2026-01-28
 */
@Data
@Schema(description = "部门树查询请求参数")
public class DeptTreeQueryDto {

    @Schema(description = "部门名称（模糊查询）", example = "技术部")
    private String deptName;

    @Schema(description = "部门编码（模糊查询）", example = "TECH")
    private String deptCode;

    @Schema(description = "状态：0-禁用，1-启用", example = "1")
    private Integer status;
}

