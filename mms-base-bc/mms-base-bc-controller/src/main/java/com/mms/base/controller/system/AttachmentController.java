package com.mms.base.controller.system;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.system.dto.AttachmentBatchDeleteDto;
import com.mms.base.common.system.dto.AttachmentCreateDto;
import com.mms.base.common.system.dto.AttachmentPageQueryDto;
import com.mms.base.common.system.dto.AttachmentStatusSwitchDto;
import com.mms.base.common.system.dto.AttachmentUpdateDto;
import com.mms.base.common.system.vo.AttachmentVo;
import com.mms.base.service.system.service.AttachmentService;
import com.mms.common.core.constants.usercenter.PermissionConstants;
import com.mms.common.core.response.Response;
import com.mms.common.security.annotations.RequiresPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 实现功能【附件管理 Controller】
 * <p>
 * 提供附件管理的REST API接口
 * </p>
 *
 * @author li.hongyu
 * @date 2026-02-06 14:36:38
 */
@Tag(name = "附件管理", description = "附件管理相关接口")
@RestController
@RequestMapping("/attachment")
public class AttachmentController {

    @Resource
    private AttachmentService attachmentService;

    @Operation(summary = "分页查询附件列表", description = "根据条件分页查询附件列表")
    @RequiresPermission(PermissionConstants.ATTACHMENT_VIEW)
    @PostMapping("/page")
    public Response<Page<AttachmentVo>> getAttachmentPage(@RequestBody @Valid AttachmentPageQueryDto dto) {
        return Response.success(attachmentService.getAttachmentPage(dto));
    }

    @Operation(summary = "根据ID查询附件详情", description = "根据附件ID查询附件详细信息")
    @RequiresPermission(PermissionConstants.ATTACHMENT_VIEW)
    @GetMapping("/{attachmentId}")
    public Response<AttachmentVo> getAttachmentById(@PathVariable Long attachmentId) {
        return Response.success(attachmentService.getAttachmentById(attachmentId));
    }

    @Operation(summary = "创建附件记录（元数据）", description = "创建附件元数据记录（不包含实际文件上传流程）")
    @RequiresPermission(PermissionConstants.ATTACHMENT_UPLOAD)
    @PostMapping("/create")
    public Response<AttachmentVo> createAttachment(@RequestBody @Valid AttachmentCreateDto dto) {
        return Response.success(attachmentService.createAttachment(dto));
    }

    @Operation(summary = "更新附件信息", description = "更新附件信息（如绑定业务、备注、状态等）")
    @RequiresPermission(PermissionConstants.ATTACHMENT_UPDATE)
    @PutMapping("/update")
    public Response<AttachmentVo> updateAttachment(@RequestBody @Valid AttachmentUpdateDto dto) {
        return Response.success(attachmentService.updateAttachment(dto));
    }

    @Operation(summary = "删除附件", description = "逻辑删除附件（软删除）")
    @RequiresPermission(PermissionConstants.ATTACHMENT_DELETE)
    @DeleteMapping("/{attachmentId}")
    public Response<Void> deleteAttachment(@PathVariable Long attachmentId) {
        attachmentService.deleteAttachment(attachmentId);
        return Response.success();
    }

    @Operation(summary = "批量删除附件", description = "批量逻辑删除附件（软删除）")
    @RequiresPermission(PermissionConstants.ATTACHMENT_DELETE)
    @PostMapping("/batch-delete")
    public Response<Void> batchDeleteAttachment(@RequestBody @Valid AttachmentBatchDeleteDto dto) {
        attachmentService.batchDeleteAttachment(dto);
        return Response.success();
    }

    @Operation(summary = "切换附件状态", description = "启用或禁用附件")
    @RequiresPermission(PermissionConstants.ATTACHMENT_UPDATE)
    @PostMapping("/switch-status")
    public Response<Void> switchAttachmentStatus(@RequestBody @Valid AttachmentStatusSwitchDto dto) {
        attachmentService.switchAttachmentStatus(dto);
        return Response.success();
    }
}

