package com.mms.usercenter.common.message.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;

/**
 * 实现功能【系统公告发件实体】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
@Data
@TableName("msg_sys_announce")
@Schema(description = "系统公告发件实体")
public class MsgSysAnnounceEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "公告标题")
    private String title;

    @TableField("content_html")
    @Schema(description = "净化后的富文本")
    private String contentHtml;

    @TableField("content_text")
    @Schema(description = "纯文本摘要")
    private String contentText;

    @TableField("scope_type")
    @Schema(description = "范围：1指定人 2角色 3全员")
    private Integer scopeType;

    @TableField("scope_payload")
    @Schema(description = "范围快照 JSON")
    private String scopePayload;

    @Schema(description = "状态：0待发送 1发送中 2已完成 3失败 4已撤回")
    private Integer status;

    @TableField("total_target")
    @Schema(description = "目标人数")
    private Integer totalTarget;

    @TableField("success_count")
    @Schema(description = "成功人数")
    private Integer successCount;

    @TableField("fail_count")
    @Schema(description = "失败人数")
    private Integer failCount;

    @TableField("cursor_json")
    @Schema(description = "扇出进度 JSON")
    private String cursorJson;

    @TableField("error_msg")
    @Schema(description = "错误信息")
    private String errorMsg;
}
