package com.mms.base.common.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【记账初始化模板-账户 VO】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Data
@Schema(description = "记账初始化模板-账户响应对象")
public class FinanceTplAccountVo {

    @Schema(description = "模板ID")
    private Long id;

    @Schema(description = "账户名称")
    private String name;

    @Schema(description = "账户类型")
    private String accountType;

    @Schema(description = "备注")
    private String note;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "是否启用：1/0")
    private Integer enabled;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
