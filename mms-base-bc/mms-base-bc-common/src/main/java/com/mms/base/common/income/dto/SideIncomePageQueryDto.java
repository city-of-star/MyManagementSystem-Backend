package com.mms.base.common.income.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 实现功能【副业收入分页查询请求 DTO】
 *
 * @author li.hongyu
 * @date 2026-07-29
 */
@Data
@Schema(description = "副业收入分页查询请求参数")
public class SideIncomePageQueryDto {

    @Schema(description = "页码，从1开始", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "来源：self/partner/other")
    private String sourceType;

    @Schema(description = "状态：paid/pending")
    private String status;

    @Schema(description = "业务日期开始")
    private LocalDate recordDateStart;

    @Schema(description = "业务日期结束")
    private LocalDate recordDateEnd;

    @Schema(description = "备注关键字")
    private String note;
}
