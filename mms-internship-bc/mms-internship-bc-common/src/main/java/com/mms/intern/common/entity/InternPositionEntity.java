package com.mms.intern.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("intern_position")
public class InternPositionEntity extends BaseEntity {

    @TableField("batch_id")
    private Long batchId;

    @TableField("enterprise_id")
    private Long enterpriseId;

    private String title;

    private Integer quota;

    private String requirement;

    @TableField("start_date")
    private LocalDate startDate;

    @TableField("end_date")
    private LocalDate endDate;

    private String status;

    private String remark;
}
