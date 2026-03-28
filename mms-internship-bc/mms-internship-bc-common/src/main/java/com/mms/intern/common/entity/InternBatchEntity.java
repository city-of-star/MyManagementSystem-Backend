package com.mms.intern.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("intern_batch")
public class InternBatchEntity extends BaseEntity {

    @TableField("batch_name")
    private String batchName;

    @TableField("school_year")
    private String schoolYear;

    private String term;

    @TableField("sign_up_start")
    private LocalDateTime signUpStart;

    @TableField("sign_up_end")
    private LocalDateTime signUpEnd;

    private Integer active;

    private String remark;
}
