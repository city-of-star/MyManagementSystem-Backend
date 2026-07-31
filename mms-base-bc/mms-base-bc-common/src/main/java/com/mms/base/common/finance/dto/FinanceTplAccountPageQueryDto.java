package com.mms.base.common.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 实现功能【记账初始化模板-账户分页查询 DTO】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Data
@Schema(description = "记账初始化模板-账户分页查询参数")
public class FinanceTplAccountPageQueryDto {

    @Schema(description = "页码，从1开始", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "账户名称关键字")
    private String name;

    @Schema(description = "账户类型")
    private String accountType;

    @Schema(description = "是否启用：1/0")
    private Integer enabled;
}
