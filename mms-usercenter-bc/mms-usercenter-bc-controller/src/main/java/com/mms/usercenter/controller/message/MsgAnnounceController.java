package com.mms.usercenter.controller.message;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.core.response.Response;
import com.mms.common.security.servlet.annotations.RequiresPermission;
import com.mms.common.security.servlet.constants.PermissionConstants;
import com.mms.usercenter.common.message.dto.MsgAnnounceCreateDto;
import com.mms.usercenter.common.message.dto.MsgAnnouncePageQueryDto;
import com.mms.usercenter.common.message.dto.MsgAnnounceUpdateDto;
import com.mms.usercenter.common.message.dto.MsgAnnounceUserPageQueryDto;
import com.mms.usercenter.common.message.vo.MsgAnnounceUserVo;
import com.mms.usercenter.common.message.vo.MsgAnnounceVo;
import com.mms.usercenter.service.message.service.MsgAnnounceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 实现功能【系统公告管理 Controller】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
@Tag(name = "系统公告", description = "系统公告管理相关接口")
@RestController
@RequestMapping("/msg/announce")
public class MsgAnnounceController {

    @Resource
    private MsgAnnounceService msgAnnounceService;

    @Operation(summary = "分页查询公告")
    @RequiresPermission(PermissionConstants.MESSAGE_ANNOUNCE_VIEW)
    @PostMapping("/page")
    public Response<Page<MsgAnnounceVo>> getAnnouncePage(@RequestBody @Valid MsgAnnouncePageQueryDto dto) {
        return Response.success(msgAnnounceService.getAnnouncePage(dto));
    }

    @Operation(summary = "公告详情")
    @RequiresPermission(PermissionConstants.MESSAGE_ANNOUNCE_VIEW)
    @GetMapping("/{id}")
    public Response<MsgAnnounceVo> getAnnounceById(@PathVariable Long id) {
        return Response.success(msgAnnounceService.getAnnounceById(id));
    }

    @Operation(summary = "发布公告")
    @RequiresPermission(PermissionConstants.MESSAGE_ANNOUNCE_CREATE)
    @PostMapping("/create")
    public Response<MsgAnnounceVo> createAnnounce(@RequestBody @Valid MsgAnnounceCreateDto dto) {
        return Response.success(msgAnnounceService.createAnnounce(dto));
    }

    @Operation(summary = "修改公告")
    @RequiresPermission(PermissionConstants.MESSAGE_ANNOUNCE_UPDATE)
    @PutMapping("/{id}")
    public Response<MsgAnnounceVo> updateAnnounce(@PathVariable Long id,
                                                  @RequestBody @Valid MsgAnnounceUpdateDto dto) {
        return Response.success(msgAnnounceService.updateAnnounce(id, dto));
    }

    @Operation(summary = "撤回公告")
    @RequiresPermission(PermissionConstants.MESSAGE_ANNOUNCE_RECALL)
    @PostMapping("/{id}/recall")
    public Response<Void> recallAnnounce(@PathVariable Long id) {
        msgAnnounceService.recallAnnounce(id);
        return Response.success();
    }

    @Operation(summary = "删除公告")
    @RequiresPermission(PermissionConstants.MESSAGE_ANNOUNCE_DELETE)
    @DeleteMapping("/{id}")
    public Response<Void> deleteAnnounce(@PathVariable Long id) {
        msgAnnounceService.deleteAnnounce(id);
        return Response.success();
    }

    @Operation(summary = "重试发送公告")
    @RequiresPermission(PermissionConstants.MESSAGE_ANNOUNCE_RETRY)
    @PostMapping("/{id}/retry")
    public Response<Void> retryAnnounce(@PathVariable Long id) {
        msgAnnounceService.retryAnnounce(id);
        return Response.success();
    }

    @Operation(summary = "已读用户分页")
    @RequiresPermission(PermissionConstants.MESSAGE_ANNOUNCE_VIEW)
    @PostMapping("/{id}/read-users")
    public Response<Page<MsgAnnounceUserVo>> pageReadUsers(@PathVariable Long id,
                                                          @RequestBody @Valid MsgAnnounceUserPageQueryDto dto) {
        return Response.success(msgAnnounceService.pageReadUsers(id, dto));
    }

    @Operation(summary = "未读用户分页")
    @RequiresPermission(PermissionConstants.MESSAGE_ANNOUNCE_VIEW)
    @PostMapping("/{id}/unread-users")
    public Response<Page<MsgAnnounceUserVo>> pageUnreadUsers(@PathVariable Long id,
                                                            @RequestBody @Valid MsgAnnounceUserPageQueryDto dto) {
        return Response.success(msgAnnounceService.pageUnreadUsers(id, dto));
    }
}
