package com.mms.base.common.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【附件信息响应 VO】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-06 14:36:38
 */
@Data
@Schema(description = "附件信息响应对象")
public class AttachmentVo {

    @Schema(description = "附件ID", example = "1")
    private Long id;

    @Schema(description = "文件名（存储文件名）", example = "20260206_abcdef.png")
    private String fileName;

    @Schema(description = "原始文件名", example = "avatar.png")
    private String originalName;

    @Schema(description = "文件存储路径", example = "/data/upload/2026/02/06/")
    private String filePath;

    @Schema(description = "文件访问URL", example = "https://cdn.xxx.com/2026/02/06/abcdef.png")
    private String fileUrl;

    @Schema(description = "文件大小（字节）", example = "1024")
    private Long fileSize;

    @Schema(description = "文件类型（扩展名）", example = "png")
    private String fileType;

    @Schema(description = "MIME类型", example = "image/png")
    private String mimeType;

    @Schema(description = "存储类型：local-本地，oss-对象存储", example = "local")
    private String storageType;

    @Schema(description = "业务类型（用于区分不同业务场景）", example = "USER_AVATAR")
    private String businessType;

    @Schema(description = "关联业务ID", example = "1")
    private Long businessId;

    @Schema(description = "状态：0-禁用，1-启用", example = "1")
    private Integer status;

    @Schema(description = "备注", example = "用户头像")
    private String remark;

    @Schema(description = "创建人ID", example = "1")
    private Long createBy;

    @Schema(description = "创建时间", example = "2026-02-06 11:21:50")
    private LocalDateTime createTime;

    @Schema(description = "更新人ID", example = "1")
    private Long updateBy;

    @Schema(description = "更新时间", example = "2026-02-06 11:21:50")
    private LocalDateTime updateTime;
}

