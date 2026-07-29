package com.mms.base.common.income.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 实现功能【批量删除副业收入记录请求 DTO】
 *
 * @author li.hongyu
 * @date 2026-07-29
 */
@Data
@Schema(description = "批量删除副业收入记录请求参数")
public class SideIncomeBatchDeleteDto {

    @NotEmpty(message = "记录ID列表不能为空")
    @Schema(description = "记录ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> ids;
}
