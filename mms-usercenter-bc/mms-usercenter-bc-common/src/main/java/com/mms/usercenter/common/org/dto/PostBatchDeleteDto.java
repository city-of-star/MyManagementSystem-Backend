package com.mms.usercenter.common.org.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 实现功能【批量删除岗位请求 DTO】
 * <p>
 * 用于批量删除岗位的请求参数
 * </p>
 *
 * @author li.hongyu
 * @date 2026-01-23 10:32:27
 */
@Data
@Schema(description = "批量删除岗位请求参数")
public class PostBatchDeleteDto {

    @NotEmpty(message = "岗位ID列表不能为空")
    @Schema(description = "岗位ID列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[1, 2, 3]")
    private List<Long> postIds;
}
