package com.mms.usercenter.common.message.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 实现功能【私信找人用户 VO】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-04 23:00:00
 */
@Data
@Schema(description = "私信找人用户")
public class MsgDmUserVo {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "主部门名称")
    private String primaryDeptName;
}
