package com.mms.base.common.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 实现功能【周期记账模板分页查询请求 DTO】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Data
@Schema(description = "周期记账模板分页查询请求参数")
public class FinanceRecurringPageQueryDto {

    @Schema(description = "页码，从1开始", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "模板名称关键字")
    private String name;

    @Schema(description = "方向：income/expense/transfer")
    private String direction;

    @Schema(description = "是否启用：1/0")
    private Integer enabled;
}
