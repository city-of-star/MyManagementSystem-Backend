package com.mms.base.common.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 实现功能【基金持仓分页查询 DTO】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Data
@Schema(description = "基金持仓分页查询请求参数")
public class FinanceFundHoldingPageQueryDto {

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "基金名称关键字")
    private String fundName;

    @Schema(description = "基金账户壳ID")
    private Long accountId;

    @Schema(description = "分类")
    private String fundCategory;

    @Schema(description = "估值状态")
    private String quoteStatus;

    @Schema(description = "是否启用：1/0")
    private Integer enabled;
}
