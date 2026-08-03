package com.mms.usercenter.controller.message;

import com.mms.common.core.response.Response;
import com.mms.common.security.servlet.annotations.RequiresPermission;
import com.mms.common.security.servlet.constants.PermissionConstants;
import com.mms.usercenter.common.message.vo.MsgUnreadVo;
import com.mms.usercenter.service.message.service.MsgUnreadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 实现功能【消息未读 Controller】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
@Tag(name = "消息未读", description = "消息未读数相关接口")
@RestController
@RequestMapping("/msg")
public class MsgUnreadController {

    @Resource
    private MsgUnreadService msgUnreadService;

    @Operation(summary = "查询当前用户未读数")
    @RequiresPermission(PermissionConstants.MESSAGE_VIEW)
    @GetMapping("/unread")
    public Response<MsgUnreadVo> getUnread() {
        return Response.success(msgUnreadService.getCurrentUnread());
    }
}
