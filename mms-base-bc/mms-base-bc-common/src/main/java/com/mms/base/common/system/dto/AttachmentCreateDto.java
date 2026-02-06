package com.mms.base.common.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 实现功能【创建附件记录请求 DTO】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-02-06 14:36:38
 */
@Data
@Schema(description = "创建附件记录请求参数")
public class AttachmentCreateDto {

    @NotBlank(message = "存储文件名不能为空")
    @Size(max = 255, message = "存储文件名长度不能超过255个字符")
    @Schema(description = "文件名（存储文件名）", requiredMode = Schema.RequiredMode.REQUIRED, example = "20260206_abcdef.png")
    private String fileName;

    @NotBlank(message = "原始文件名不能为空")
    @Size(max = 255, message = "原始文件名长度不能超过255个字符")
    @Schema(description = "原始文件名", requiredMode = Schema.RequiredMode.REQUIRED, example = "avatar.png")
    private String originalName;

    @NotBlank(message = "文件存储路径不能为空")
    @Size(max = 1024, message = "文件存储路径长度不能超过1024个字符")
    @Schema(description = "文件存储路径", requiredMode = Schema.RequiredMode.REQUIRED, example = "/data/upload/2026/02/06/")
    private String filePath;

    @NotBlank(message = "文件访问URL不能为空")
    @Size(max = 1024, message = "文件访问URL长度不能超过1024个字符")
    @Schema(description = "文件访问URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://cdn.xxx.com/2026/02/06/abcdef.png")
    private String fileUrl;

    @NotNull(message = "文件大小不能为空")
    @Schema(description = "文件大小（字节）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long fileSize;

    @NotBlank(message = "文件类型不能为空")
    @Size(max = 64, message = "文件类型长度不能超过64个字符")
    @Schema(description = "文件类型（扩展名）", requiredMode = Schema.RequiredMode.REQUIRED, example = "png")
    private String fileType;

    @Size(max = 128, message = "MIME类型长度不能超过128个字符")
    @Schema(description = "MIME类型", example = "image/png")
    private String mimeType;

    @Schema(description = "存储类型：local-本地，oss-对象存储，默认为local", example = "local")
    private String storageType = "local";

    @Size(max = 64, message = "业务类型长度不能超过64个字符")
    @Schema(description = "业务类型（用于区分不同业务场景）", example = "USER_AVATAR")
    private String businessType;

    @Schema(description = "关联业务ID", example = "1")
    private Long businessId;

    @Schema(description = "状态：0-禁用，1-启用，默认为1", example = "1")
    private Integer status = 1;

    @Size(max = 512, message = "备注长度不能超过512个字符")
    @Schema(description = "备注", example = "用户头像")
    private String remark;
}

