package com.mms.base.common.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;

/**
 * 实现功能【数据字典数据分页查询请求 DTO】
 * <p>
 * 用于分页查询数据字典数据列表的请求参数
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-23 11:21:50
 */
@Data
@Schema(description = "数据字典数据分页查询请求参数")
public class DictDataPageQueryDto {

    @Schema(description = "页码，从1开始", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "字典类型ID", example = "1")
    private Long dictTypeId;

    @Schema(description = "字典标签（模糊查询）", example = "启用")
    private String dictLabel;

    @Schema(description = "字典值（模糊查询）", example = "1")
    private String dictValue;

    @Range(min = 0, max = 1, message = "状态值只能是0或1")
    @Schema(description = "状态：0-禁用，1-启用", example = "1")
    private Integer status;

    @Schema(description = "创建时间开始", example = "2025-01-01 00:00:00")
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间结束", example = "2025-12-31 23:59:59")
    private LocalDateTime createTimeEnd;
}

