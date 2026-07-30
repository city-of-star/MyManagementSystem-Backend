package com.mms.base.common.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 实现功能【记账分类统计响应 VO】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Data
@Schema(description = "记账分类统计响应对象")
public class FinanceCategoryStatVo {

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "金额合计")
    private BigDecimal amount;
}
