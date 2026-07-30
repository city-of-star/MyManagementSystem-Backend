package com.mms.base.common.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 实现功能【记账分类列表查询请求 DTO】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Data
@Schema(description = "记账分类列表查询请求参数")
public class FinanceCategoryListQueryDto {

    @Schema(description = "方向：income/expense")
    private String direction;

    @Schema(description = "是否启用：1/0")
    private Integer enabled;
}
