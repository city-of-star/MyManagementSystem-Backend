package com.mms.intern.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("intern_enterprise")
public class InternEnterpriseEntity extends BaseEntity {

    @TableField("enterprise_name")
    private String enterpriseName;

    @TableField("credit_code")
    private String creditCode;

    @TableField("contact_name")
    private String contactName;

    @TableField("contact_phone")
    private String contactPhone;

    private String address;

    private String intro;

    @TableField("audit_status")
    private String auditStatus;

    @TableField("audit_remark")
    private String auditRemark;

    @TableField("audit_by")
    private Long auditBy;

    @TableField("audit_time")
    private LocalDateTime auditTime;

    private Integer status;

    private String remark;
}
