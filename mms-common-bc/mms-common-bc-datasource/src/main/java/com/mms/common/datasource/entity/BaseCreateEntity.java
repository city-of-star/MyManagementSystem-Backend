package com.mms.common.datasource.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 实现功能【基础实体类】
 * <p>
 * 继承自 BaseIdEntity
 * 适用于：只有创建人和创建时间审计字段的表
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-12 14:40:36
 */
@Data
public class BaseCreateEntity extends BaseIdEntity{

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 创建人ID
     * 插入时自动填充
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 创建时间
     * 插入时自动填充
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}