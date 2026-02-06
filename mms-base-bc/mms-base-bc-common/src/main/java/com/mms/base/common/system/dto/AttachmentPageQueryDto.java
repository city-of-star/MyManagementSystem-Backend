package com.mms.base.common.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【附件分页查询请求 DTO】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-06 14:36:38
 */
@Data
@Schema(description = "附件分页查询请求参数")
public class AttachmentPageQueryDto {

    @Schema(description = "页码，从1开始", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "原始文件名（模糊查询）", example = "avatar")
    private String originalName;

    @Schema(description = "文件类型（扩展名）", example = "png")
    private String fileType;

    @Schema(description = "MIME类型", example = "image/png")
    private String mimeType;

    @Schema(description = "存储类型：local-本地，oss-对象存储", example = "local")
    private String storageType;

    @Schema(description = "业务类型", example = "USER_AVATAR")
    private String businessType;

    @Schema(description = "关联业务ID", example = "1")
    private Long businessId;

    @Schema(description = "状态：0-禁用，1-启用", example = "1")
    private Integer status;

    @Schema(description = "创建人ID", example = "1")
    private Long createBy;

    @Schema(description = "创建时间开始", example = "2025-01-01 00:00:00")
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间结束", example = "2025-12-31 23:59:59")
    private LocalDateTime createTimeEnd;
}

