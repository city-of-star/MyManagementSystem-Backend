package com.mms.usercenter.common.org.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 实现功能【批量删除部门请求 DTO】
 * <p>
 * 用于批量删除部门的请求参数
 * </p>
 *
 * @author li.hongyu
 * @date 2026-01-23 10:31:08
 */
@Data
@Schema(description = "批量删除部门请求参数")
public class DeptBatchDeleteDto {

    @NotEmpty(message = "部门ID列表不能为空")
    @Schema(description = "部门ID列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[1, 2, 3]")
    private List<Long> deptIds;
}
