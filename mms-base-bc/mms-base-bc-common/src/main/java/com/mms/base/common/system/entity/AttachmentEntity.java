package com.mms.base.common.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;

/**
 * 实现功能【附件实体】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-06 14:36:38
 */
@Data
@TableName("attachment")
@Schema(description = "附件实体")
public class AttachmentEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("file_name")
    @Schema(description = "文件名（存储文件名）")
    private String fileName;

    @TableField("original_name")
    @Schema(description = "原始文件名")
    private String originalName;

    @TableField("file_path")
    @Schema(description = "文件存储路径")
    private String filePath;

    @TableField("file_url")
    @Schema(description = "文件访问URL")
    private String fileUrl;

    @TableField("file_size")
    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    @TableField("file_type")
    @Schema(description = "文件类型（扩展名）")
    private String fileType;

    @TableField("mime_type")
    @Schema(description = "MIME类型")
    private String mimeType;

    @TableField("storage_type")
    @Schema(description = "存储类型：local-本地，oss-对象存储")
    private String storageType;

    @TableField("business_type")
    @Schema(description = "业务类型（用于区分不同业务场景）")
    private String businessType;

    @TableField("business_id")
    @Schema(description = "关联业务ID")
    private Long businessId;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}

