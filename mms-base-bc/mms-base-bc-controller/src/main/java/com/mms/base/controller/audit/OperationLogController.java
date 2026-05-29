package com.mms.base.controller.audit;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.audit.dto.OperationLogBatchDeleteDto;
import com.mms.base.common.audit.dto.OperationLogPageQueryDto;
import com.mms.base.common.audit.vo.OperationLogVo;
import com.mms.base.service.audit.service.OperationLogService;
import com.mms.common.core.constants.usercenter.PermissionConstants;
import com.mms.common.core.response.Response;
import com.mms.common.core.utils.DateUtils;
import com.mms.common.security.servlet.annotations.RequiresPermission;
import com.mms.common.webmvc.file.FileDownloadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 实现功能【用户操作日志 Controller】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-05-29 10:00:00
 */
@Tag(name = "用户操作日志", description = "用户操作日志相关接口")
@RestController
@RequestMapping("/audit/operation-log")
public class OperationLogController {

    @Resource
    private OperationLogService operationLogService;

    @Resource
    private FileDownloadService fileDownloadService;

    @Operation(summary = "分页查询用户操作日志", description = "根据条件分页查询用户操作日志")
    @RequiresPermission(PermissionConstants.AUDIT_OPERATION_LOG_VIEW)
    @PostMapping("/page")
    public Response<Page<OperationLogVo>> getOperationLogPage(@RequestBody @Valid OperationLogPageQueryDto dto) {
        return Response.success(operationLogService.getOperationLogPage(dto));
    }

    @Operation(summary = "根据ID查询用户操作日志详情", description = "根据操作日志ID查询详情")
    @RequiresPermission(PermissionConstants.AUDIT_OPERATION_LOG_VIEW)
    @GetMapping("/{logId}")
    public Response<OperationLogVo> getOperationLogById(@PathVariable Long logId) {
        return Response.success(operationLogService.getOperationLogById(logId));
    }

    @Operation(summary = "删除用户操作日志", description = "根据ID删除单条用户操作日志")
    @RequiresPermission(PermissionConstants.AUDIT_OPERATION_LOG_DELETE)
    @DeleteMapping("/{logId}")
    public Response<Void> deleteOperationLog(@PathVariable Long logId) {
        operationLogService.deleteOperationLog(logId);
        return Response.success();
    }

    @Operation(summary = "批量删除用户操作日志", description = "批量删除用户操作日志")
    @RequiresPermission(PermissionConstants.AUDIT_OPERATION_LOG_DELETE)
    @PostMapping("/batch-delete")
    public Response<Void> batchDeleteOperationLog(@RequestBody @Valid OperationLogBatchDeleteDto dto) {
        operationLogService.batchDeleteOperationLog(dto);
        return Response.success();
    }

    @Operation(summary = "导出用户操作日志", description = "导出用户操作日志")
    @RequiresPermission(PermissionConstants.AUDIT_OPERATION_LOG_EXPORT)
    @PostMapping("/export")
    public void exportOperationLog(@RequestBody @Valid OperationLogPageQueryDto dto, HttpServletResponse response) {
        byte[] fileBytes = operationLogService.exportOperationLog(dto);
        String fileName = "用户操作日志_" + DateUtils.formatDate(DateUtils.today()) + ".xlsx";
        fileDownloadService.writeExcel(response, fileBytes, fileName);
    }
}
