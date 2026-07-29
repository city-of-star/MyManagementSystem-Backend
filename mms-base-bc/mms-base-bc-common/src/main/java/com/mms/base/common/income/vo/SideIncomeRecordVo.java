package com.mms.base.common.income.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 实现功能【副业收入记录响应 VO】
 *
 * @author li.hongyu
 * @date 2026-07-29
 */
@Data
@Schema(description = "副业收入记录响应对象")
public class SideIncomeRecordVo {

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "业务发生日期")
    private LocalDate recordDate;

    @Schema(description = "应得金额（元）")
    private BigDecimal amount;

    @Schema(description = "整单流水（元）")
    private BigDecimal grossAmount;

    @Schema(description = "来源：self/partner/other")
    private String sourceType;

    @Schema(description = "状态：paid/pending")
    private String status;

    @Schema(description = "备注")
    private String note;

    @Schema(description = "创建人ID")
    private Long createBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新人ID")
    private Long updateBy;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
