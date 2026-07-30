package com.mms.base.common.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 实现功能【记账流水分页查询请求 DTO】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Data
@Schema(description = "记账流水分页查询请求参数")
public class FinanceTransactionPageQueryDto {

    @Schema(description = "页码，从1开始", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "交易类型：income/expense/transfer")
    private String txnType;

    @Schema(description = "状态：settled/pending")
    private String status;

    @Schema(description = "账户ID")
    private Long accountId;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "交易日期开始")
    private LocalDate txnDateStart;

    @Schema(description = "交易日期结束")
    private LocalDate txnDateEnd;

    @Schema(description = "备注关键字")
    private String note;
}
