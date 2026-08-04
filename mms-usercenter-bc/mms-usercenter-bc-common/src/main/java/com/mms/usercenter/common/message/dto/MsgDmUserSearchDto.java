package com.mms.usercenter.common.message.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 实现功能【私信找人查询 DTO】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-04 23:00:00
 */
@Data
@Schema(description = "私信找人查询")
public class MsgDmUserSearchDto {

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "30")
    private Integer pageSize = 30;

    @Schema(description = "关键词：用户名/昵称/真实姓名/主部门名")
    private String keyword;
}
