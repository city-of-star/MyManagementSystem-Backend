package com.mms.usercenter.common.org.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 实现功能【切换岗位状态请求 DTO】
 * <p>
 * 用于启用/禁用岗位的请求参数
 * </p>
 *
 * @author li.hongyu
 * @date 2026-01-23 10:32:14
 */
@Data
@Schema(description = "切换岗位状态请求参数")
public class PostStatusSwitchDto {

    @NotNull(message = "岗位ID不能为空")
    @Schema(description = "岗位ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long postId;

    @NotNull(message = "状态不能为空")
    @Schema(description = "状态：0-禁用，1-启用", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;
}
