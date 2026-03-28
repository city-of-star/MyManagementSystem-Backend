package com.mms.intern.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("intern_application")
public class InternApplicationEntity extends BaseEntity {

    @TableField("batch_id")
    private Long batchId;

    @TableField("position_id")
    private Long positionId;

    @TableField("student_user_id")
    private Long studentUserId;

    @TableField("school_mentor_user_id")
    private Long schoolMentorUserId;

    @TableField("enterprise_mentor_user_id")
    private Long enterpriseMentorUserId;

    private String status;

    @TableField("audit_remark")
    private String auditRemark;

    @TableField("audit_by")
    private Long auditBy;

    @TableField("audit_time")
    private LocalDateTime auditTime;

    private String remark;
}
