package com.mms.common.datasource.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 实现功能【基础实体类】
 * <p>
 * 继承自 BaseIdEntity
 * 适用于：有完整审计字段的表
 * </p>
 *
 * @author li.hongyu
 * @date 2026-01-07
 */
@Data
public class BaseEntity extends BaseIdEntity {

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

    /**
     * 更新人ID
     * 插入和更新时自动填充
     */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /**
     * 更新时间
     * 插入和更新时自动填充
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记
     * 0-未删除，1-已删除
     * MyBatis Plus 会自动处理逻辑删除
     */
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;
}
