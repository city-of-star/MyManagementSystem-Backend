package com.mms.usercenter.common.org.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 实现功能【切换部门状态请求 DTO】
 * <p>
 * 用于启用/禁用部门的请求参数
 * </p>
 *
 * @author li.hongyu
 * @date 2026-01-23 10:30:55
 */
@Data
@Schema(description = "切换部门状态请求参数")
public class DeptStatusSwitchDto {

    @NotNull(message = "部门ID不能为空")
    @Schema(description = "部门ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long deptId;

    @NotNull(message = "状态不能为空")
    @Schema(description = "状态：0-禁用，1-启用", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;
}
