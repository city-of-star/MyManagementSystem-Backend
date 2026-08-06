package com.mms.usercenter.common.message.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 实现功能【修改系统公告请求 DTO】
 * <p>
 * 已发布公告允许改标题、正文、跳转路径；范围不变，并同步更新收件箱副本。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-04 17:50:00
 */
@Data
@Schema(description = "修改系统公告请求参数")
public class MsgAnnounceUpdateDto {

    @NotBlank(message = "公告标题不能为空")
    @Schema(description = "公告标题")
    private String title;

    @NotBlank(message = "公告内容不能为空")
    @Schema(description = "公告富文本内容")
    private String contentHtml;

    @Size(max = 200, message = "跳转路径长度不能超过200个字符")
    @Schema(description = "可选站内跳转路径，清空则取消跳转")
    private String linkPath;
}
