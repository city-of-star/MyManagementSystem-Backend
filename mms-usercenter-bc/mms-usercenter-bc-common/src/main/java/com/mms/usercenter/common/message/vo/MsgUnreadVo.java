package com.mms.usercenter.common.message.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 实现功能【未读数 VO】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
@Data
@Schema(description = "消息未读数")
public class MsgUnreadVo {

    @Schema(description = "未读总数")
    private Integer total;

    @Schema(description = "系统通知未读")
    private Integer sysUnread;

    @Schema(description = "私信未读")
    private Integer privUnread;
}
