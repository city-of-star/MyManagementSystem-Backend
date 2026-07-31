package com.mms.base.common.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【记账初始化模板-分类 VO】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Data
@Schema(description = "记账初始化模板-分类响应对象")
public class FinanceTplCategoryVo {

    @Schema(description = "模板ID")
    private Long id;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "方向：income/expense")
    private String direction;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "是否启用：1/0")
    private Integer enabled;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
