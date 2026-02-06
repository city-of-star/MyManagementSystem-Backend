package com.mms.base.common.system.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

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
public class AttachmentEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "附件ID")
    private Long id;

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

    @TableLogic(value = "0", delval = "1")
    @Schema(description = "是否删除：0-未删除，1-已删除")
    private Integer deleted;

    @TableField(value = "create_by", fill = FieldFill.INSERT)
    @Schema(description = "创建人ID")
    private Long createBy;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新人ID")
    private Long updateBy;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}

