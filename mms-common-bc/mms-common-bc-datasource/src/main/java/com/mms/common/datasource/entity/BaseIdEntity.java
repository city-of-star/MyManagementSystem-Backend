package com.mms.common.datasource.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 实现功能【基础实体类】
 * <p>
 * 适用于：没有审计字段的表（如日志表、锁表等）
 * </p>
 *
 * @author li.hongyu
 * @date 2026-01-07
 */
@Data
public class BaseIdEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     * 使用雪花算法生成
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

}
