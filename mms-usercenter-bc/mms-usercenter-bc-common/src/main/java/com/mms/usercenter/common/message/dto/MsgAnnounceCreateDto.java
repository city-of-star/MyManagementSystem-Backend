package com.mms.usercenter.common.message.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 实现功能【发布系统公告请求 DTO】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
@Data
@Schema(description = "发布系统公告请求参数")
public class MsgAnnounceCreateDto {

    @NotBlank(message = "公告标题不能为空")
    @Schema(description = "公告标题", example = "系统维护通知")
    private String title;

    @NotBlank(message = "公告内容不能为空")
    @Schema(description = "公告富文本内容")
    private String contentHtml;

    @Size(max = 200, message = "跳转路径长度不能超过200个字符")
    @Schema(description = "可选站内跳转路径，如 /finance/recurrings")
    private String linkPath;

    @NotNull(message = "发送范围不能为空")
    @Schema(description = "范围：1指定人 2角色 3全员", example = "3")
    private Integer scopeType;

    @Schema(description = "指定用户ID列表（scopeType=1）")
    private List<Long> userIds;

    @Schema(description = "角色ID列表（scopeType=2）")
    private List<Long> roleIds;
}
