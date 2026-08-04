package com.mms.usercenter.controller.message;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.core.response.Response;
import com.mms.common.security.servlet.annotations.RequiresPermission;
import com.mms.common.security.servlet.constants.PermissionConstants;
import com.mms.usercenter.common.message.dto.MsgDmConversationPageQueryDto;
import com.mms.usercenter.common.message.dto.MsgDmMessagePageQueryDto;
import com.mms.usercenter.common.message.dto.MsgDmOpenDto;
import com.mms.usercenter.common.message.dto.MsgDmSendDto;
import com.mms.usercenter.common.message.dto.MsgDmUserSearchDto;
import com.mms.usercenter.common.message.vo.MsgDmConversationVo;
import com.mms.usercenter.common.message.vo.MsgDmMessageVo;
import com.mms.usercenter.common.message.vo.MsgDmUserVo;
import com.mms.usercenter.service.message.service.MsgDmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 实现功能【私信 Controller】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
@Tag(name = "私信", description = "一对一私信相关接口")
@RestController
@RequestMapping("/msg/dm")
public class MsgDmController {

    @Resource
    private MsgDmService msgDmService;

    @Operation(summary = "私信会话分页")
    @RequiresPermission(PermissionConstants.MESSAGE_VIEW)
    @PostMapping("/conversation/page")
    public Response<Page<MsgDmConversationVo>> getConversationPage(@RequestBody @Valid MsgDmConversationPageQueryDto dto) {
        return Response.success(msgDmService.getConversationPage(dto));
    }

    @Operation(summary = "按会话ID获取（深链打开）")
    @RequiresPermission(PermissionConstants.MESSAGE_VIEW)
    @GetMapping("/conversation/{id}")
    public Response<MsgDmConversationVo> getConversationById(@PathVariable("id") Long conversationId) {
        return Response.success(msgDmService.getConversationById(conversationId));
    }

    @Operation(summary = "找人打开会话")
    @RequiresPermission(PermissionConstants.MESSAGE_SEND_PRIVATE)
    @PostMapping("/conversation/open")
    public Response<MsgDmConversationVo> openConversation(@RequestBody @Valid MsgDmOpenDto dto) {
        return Response.success(msgDmService.openConversation(dto));
    }

    @Operation(summary = "私信找人（启用用户轻量搜索）")
    @RequiresPermission(PermissionConstants.MESSAGE_SEND_PRIVATE)
    @PostMapping("/user/search")
    public Response<Page<MsgDmUserVo>> searchUsers(@RequestBody @Valid MsgDmUserSearchDto dto) {
        return Response.success(msgDmService.searchUsers(dto));
    }

    @Operation(summary = "私信消息分页")
    @RequiresPermission(PermissionConstants.MESSAGE_VIEW)
    @PostMapping("/message/page")
    public Response<Page<MsgDmMessageVo>> getMessagePage(@RequestBody @Valid MsgDmMessagePageQueryDto dto) {
        return Response.success(msgDmService.getMessagePage(dto));
    }

    @Operation(summary = "发送私信")
    @RequiresPermission(PermissionConstants.MESSAGE_SEND_PRIVATE)
    @PostMapping("/message/send")
    public Response<MsgDmMessageVo> sendMessage(@RequestBody @Valid MsgDmSendDto dto) {
        return Response.success(msgDmService.sendMessage(dto));
    }

    @Operation(summary = "私信全部已读")
    @RequiresPermission(PermissionConstants.MESSAGE_READ)
    @PostMapping("/conversation/read-all")
    public Response<Void> markAllRead() {
        msgDmService.markAllRead();
        return Response.success();
    }

    @Operation(summary = "不显示会话")
    @RequiresPermission(PermissionConstants.MESSAGE_VIEW)
    @PostMapping("/conversation/{id}/hide")
    public Response<Void> hideConversation(@PathVariable("id") Long conversationId) {
        msgDmService.hideConversation(conversationId);
        return Response.success();
    }

    @Operation(summary = "置顶/取消置顶")
    @RequiresPermission(PermissionConstants.MESSAGE_VIEW)
    @PostMapping("/conversation/{id}/pin")
    public Response<Void> pinConversation(@PathVariable("id") Long conversationId,
                                          @RequestParam(defaultValue = "true") boolean pinned) {
        msgDmService.pinConversation(conversationId, pinned);
        return Response.success();
    }

    @Operation(summary = "删除会话（自己侧）")
    @RequiresPermission(PermissionConstants.MESSAGE_DELETE)
    @DeleteMapping("/conversation/{id}")
    public Response<Void> deleteConversation(@PathVariable("id") Long conversationId) {
        msgDmService.deleteConversation(conversationId);
        return Response.success();
    }
}
