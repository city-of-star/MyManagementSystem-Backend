package com.mms.intern.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("intern_weekly_log")
public class InternWeeklyLogEntity extends BaseEntity {

    @TableField("application_id")
    private Long applicationId;

    @TableField("week_index")
    private Integer weekIndex;

    private String title;

    private String content;

    @TableField("attachment_ids")
    private String attachmentIds;

    private String status;

    @TableField("review_comment")
    private String reviewComment;

    @TableField("review_by")
    private Long reviewBy;

    @TableField("review_time")
    private LocalDateTime reviewTime;
}
