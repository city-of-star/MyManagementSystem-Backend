package com.mms.usercenter.common.message.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 实现功能【业务系统通知投递结果】
 *
 * @author li.hongyu
 * @date 2026-08-06
 */
@Data
@Schema(description = "业务系统通知投递结果")
public class MsgBizNotifyVo {

    @Schema(description = "收件箱消息ID（已存在时也可能返回原ID）")
    private Long inboxId;

    @Schema(description = "本次是否新写入：true=新建，false=幂等跳过")
    private Boolean created;
}
