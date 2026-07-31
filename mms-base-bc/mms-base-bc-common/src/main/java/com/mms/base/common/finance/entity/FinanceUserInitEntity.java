package com.mms.base.common.finance.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 实现功能【记账用户初始化标记实体】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Data
@TableName("finance_user_init")
@Schema(description = "记账用户初始化标记实体")
public class FinanceUserInitEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId("user_id")
    @Schema(description = "用户ID")
    private Long userId;

    @TableField("init_time")
    @Schema(description = "初始化时间")
    private LocalDateTime initTime;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
