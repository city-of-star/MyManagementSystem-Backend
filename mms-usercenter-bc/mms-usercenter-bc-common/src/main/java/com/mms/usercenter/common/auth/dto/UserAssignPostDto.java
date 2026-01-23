package com.mms.usercenter.common.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 实现功能【用户分配岗位 DTO】
 *
 * @author li.hongyu
 * @date 2026-01-23 14:20:28
 */
@Data
@Schema(description = "用户分配岗位请求参数")
public class UserAssignPostDto {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @NotEmpty(message = "岗位ID列表不能为空")
    @Schema(description = "岗位ID列表", example = "[1, 2, 3]")
    private List<Long> postIds;

    @Schema(description = "主岗位ID，必须包含在岗位ID列表中", example = "1")
    private Long primaryPostId;
}

