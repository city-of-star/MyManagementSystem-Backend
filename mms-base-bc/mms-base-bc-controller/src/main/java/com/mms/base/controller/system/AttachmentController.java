package com.mms.base.controller.system;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.system.dto.AttachmentBatchDeleteDto;
import com.mms.base.common.system.dto.AttachmentCreateDto;
import com.mms.base.common.system.dto.AttachmentPageQueryDto;
import com.mms.base.common.system.dto.AttachmentStatusSwitchDto;
import com.mms.base.common.system.dto.AttachmentUpdateDto;
import com.mms.base.common.system.vo.AttachmentVo;
import com.mms.base.service.file.service.FileService;
import com.mms.base.service.system.service.AttachmentService;
import com.mms.common.core.constants.usercenter.PermissionConstants;
import com.mms.common.core.exceptions.ServerException;
import com.mms.common.core.response.Response;
import com.mms.common.security.annotations.RequiresPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.io.InputStream;

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

    @Resource
    private FileService fileService;

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

    @Operation(summary = "硬删除附件", description = "删除物理文件并逻辑删除附件记录，仅管理员使用")
    @RequiresPermission(PermissionConstants.ATTACHMENT_DELETE)
    @DeleteMapping("/{attachmentId}/hard")
    public Response<Void> hardDeleteAttachment(@PathVariable Long attachmentId) {
        attachmentService.hardDeleteAttachment(attachmentId);
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

    @Operation(summary = "上传附件", description = "上传文件并创建附件记录（本地存储）")
    @RequiresPermission(PermissionConstants.ATTACHMENT_UPLOAD)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<AttachmentVo> uploadAttachment(@RequestPart("file") MultipartFile file,
                                                   @RequestParam(value = "businessType", required = false) String businessType,
                                                   @RequestParam(value = "businessId", required = false) Long businessId,
                                                   @RequestParam(value = "remark", required = false) String remark) {
        return Response.success(attachmentService.uploadAttachment(file, businessType, businessId, remark));
    }

    @Operation(summary = "加载附件内容（流式输出）", description = "根据文件相对路径流式输出附件内容，用于图片/文件预览与下载")
    @GetMapping("/stream/**")
    public void loadAttachmentContent(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 通过 Spring 提供的属性解析出匹配模式中的相对路径部分
            // 示例：
            //   - 请求路径：/attachment/stream/2026/02/09/a.png
            //   - bestMatchingPattern：/attachment/stream/**
            //   - pathWithinHandler：/attachment/stream/2026/02/09/a.png
            //   - relativePath（提取结果）：2026/02/09/a.png
            String pathWithinHandler = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
            String bestMatchingPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
            String relativePath = new AntPathMatcher().extractPathWithinPattern(bestMatchingPattern, pathWithinHandler);
            // 判断是否为空
            if (!StringUtils.hasText(relativePath)) {
                throw new ServerException("附件路径不能为空");
            }
            // 判断文件是否存在
            if (!fileService.exists(relativePath)) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            // 尝试自动识别类型和大小
            response.setContentType(fileService.getContentType(relativePath) == null
                    ? MediaType.APPLICATION_OCTET_STREAM_VALUE
                    : fileService.getContentType(relativePath));
            response.setContentLengthLong(fileService.getFileSize(relativePath));
            // 写入文件流
            try (InputStream in = fileService.openStream(relativePath)) {
                StreamUtils.copy(in, response.getOutputStream());
            }
        } catch (IOException e) {
            throw new ServerException("附件读取失败", e);
        }
    }
}

