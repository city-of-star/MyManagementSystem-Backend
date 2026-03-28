package com.mms.intern.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("intern_material")
public class InternMaterialEntity extends BaseEntity {

    @TableField("application_id")
    private Long applicationId;

    @TableField("material_type")
    private String materialType;

    @TableField("material_name")
    private String materialName;

    @TableField("attachment_id")
    private Long attachmentId;

    private String status;

    @TableField("audit_remark")
    private String auditRemark;

    @TableField("audit_by")
    private Long auditBy;

    @TableField("audit_time")
    private LocalDateTime auditTime;

    private String remark;
}
