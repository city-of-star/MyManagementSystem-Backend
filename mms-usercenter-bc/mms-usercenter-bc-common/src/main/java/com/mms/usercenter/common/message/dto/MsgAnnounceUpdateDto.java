package com.mms.usercenter.common.message.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 实现功能【修改系统公告请求 DTO】
 * <p>
 * 已发布公告仅允许改标题与正文；范围不变，并同步更新收件箱副本。
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
}
