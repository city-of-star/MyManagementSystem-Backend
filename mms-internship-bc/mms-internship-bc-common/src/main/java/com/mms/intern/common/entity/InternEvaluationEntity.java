package com.mms.intern.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("intern_evaluation")
public class InternEvaluationEntity extends BaseEntity {

    @TableField("application_id")
    private Long applicationId;

    @TableField("school_score")
    private BigDecimal schoolScore;

    @TableField("school_comment")
    private String schoolComment;

    @TableField("school_by")
    private Long schoolBy;

    @TableField("school_time")
    private LocalDateTime schoolTime;

    @TableField("enterprise_score")
    private BigDecimal enterpriseScore;

    @TableField("enterprise_comment")
    private String enterpriseComment;

    @TableField("enterprise_by")
    private Long enterpriseBy;

    @TableField("enterprise_time")
    private LocalDateTime enterpriseTime;

    @TableField("final_score")
    private BigDecimal finalScore;

    @TableField("final_remark")
    private String finalRemark;
}
