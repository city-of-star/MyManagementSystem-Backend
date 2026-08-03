package com.mms.usercenter.controller.message;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.core.response.Response;
import com.mms.common.security.servlet.annotations.RequiresPermission;
import com.mms.common.security.servlet.constants.PermissionConstants;
import com.mms.usercenter.common.message.dto.MsgSysInboxPageQueryDto;
import com.mms.usercenter.common.message.dto.MsgSysInboxStarDto;
import com.mms.usercenter.common.message.vo.MsgSysInboxVo;
import com.mms.usercenter.service.message.service.MsgSysInboxService;
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
import org.springframework.web.bind.annotation.RestController;

/**
 * 实现功能【系统收件箱 Controller】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
@Tag(name = "系统收件箱", description = "系统通知收件箱相关接口")
@RestController
@RequestMapping("/msg/sys-inbox")
public class MsgSysInboxController {

    @Resource
    private MsgSysInboxService msgSysInboxService;

    @Operation(summary = "分页查询系统通知")
    @RequiresPermission(PermissionConstants.MESSAGE_VIEW)
    @PostMapping("/page")
    public Response<Page<MsgSysInboxVo>> getInboxPage(@RequestBody @Valid MsgSysInboxPageQueryDto dto) {
        return Response.success(msgSysInboxService.getInboxPage(dto));
    }

    @Operation(summary = "系统通知详情（打开即已读）")
    @RequiresPermission(PermissionConstants.MESSAGE_VIEW)
    @GetMapping("/{id}")
    public Response<MsgSysInboxVo> getInboxById(@PathVariable Long id) {
        return Response.success(msgSysInboxService.getInboxById(id));
    }

    @Operation(summary = "标记已读")
    @RequiresPermission(PermissionConstants.MESSAGE_READ)
    @PostMapping("/{id}/read")
    public Response<Void> markRead(@PathVariable Long id) {
        msgSysInboxService.markRead(id);
        return Response.success();
    }

    @Operation(summary = "全部已读")
    @RequiresPermission(PermissionConstants.MESSAGE_READ)
    @PostMapping("/read-all")
    public Response<Void> markAllRead() {
        msgSysInboxService.markAllRead();
        return Response.success();
    }

    @Operation(summary = "收藏/取消收藏")
    @RequiresPermission(PermissionConstants.MESSAGE_VIEW)
    @PostMapping("/star")
    public Response<Void> star(@RequestBody @Valid MsgSysInboxStarDto dto) {
        msgSysInboxService.star(dto);
        return Response.success();
    }

    @Operation(summary = "删除系统通知")
    @RequiresPermission(PermissionConstants.MESSAGE_DELETE)
    @DeleteMapping("/{id}")
    public Response<Void> deleteInbox(@PathVariable Long id) {
        msgSysInboxService.deleteInbox(id);
        return Response.success();
    }
}
