package com.mms.usercenter.controller.audit;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.core.constants.usercenter.PermissionConstants;
import com.mms.common.core.response.Response;
import com.mms.common.core.utils.DateUtils;
import com.mms.common.security.servlet.annotations.RequiresPermission;
import com.mms.common.webmvc.file.FileDownloadService;
import com.mms.usercenter.common.audit.dto.UserLoginLogBatchDeleteDto;
import com.mms.usercenter.common.audit.dto.UserLoginLogPageQueryDto;
import com.mms.usercenter.common.audit.vo.UserLoginLogVo;
import com.mms.usercenter.service.audit.service.UserLoginLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 实现功能【用户登录日志 Controller】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-03-24 10:54:11
 */
@Tag(name = "用户登录日志", description = "用户登录日志相关接口")
@RestController
@RequestMapping("/audit/user-login-log")
public class UserLoginLogController {

    @Resource
    private UserLoginLogService userLoginLogService;

    @Resource
    private FileDownloadService fileDownloadService;

    @Operation(summary = "分页查询用户登录日志", description = "根据条件分页查询用户登录日志")
    @RequiresPermission(PermissionConstants.AUDIT_LOGIN_LOG_VIEW)
    @PostMapping("/page")
    public Response<Page<UserLoginLogVo>> getUserLoginLogPage(@RequestBody @Valid UserLoginLogPageQueryDto dto) {
        return Response.success(userLoginLogService.getUserLoginLogPage(dto));
    }

    @Operation(summary = "根据ID查询用户登录日志详情", description = "根据登录日志ID查询详情")
    @RequiresPermission(PermissionConstants.AUDIT_LOGIN_LOG_VIEW)
    @GetMapping("/{logId}")
    public Response<UserLoginLogVo> getUserLoginLogById(@PathVariable Long logId) {
        return Response.success(userLoginLogService.getUserLoginLogById(logId));
    }

    @Operation(summary = "删除用户登录日志", description = "根据ID删除单条用户登录日志")
    @RequiresPermission(PermissionConstants.AUDIT_LOGIN_LOG_DELETE)
    @DeleteMapping("/{logId}")
    public Response<Void> deleteUserLoginLog(@PathVariable Long logId) {
        userLoginLogService.deleteUserLoginLog(logId);
        return Response.success();
    }

    @Operation(summary = "批量删除用户登录日志", description = "批量删除用户登录日志")
    @RequiresPermission(PermissionConstants.AUDIT_LOGIN_LOG_DELETE)
    @PostMapping("/batch-delete")
    public Response<Void> batchDeleteUserLoginLog(@RequestBody @Valid UserLoginLogBatchDeleteDto dto) {
        userLoginLogService.batchDeleteUserLoginLog(dto);
        return Response.success();
    }

    @Operation(summary = "导出用户登录日志", description = "导出用户登录日志")
    @RequiresPermission(PermissionConstants.AUDIT_LOGIN_LOG_EXPORT)
    @PostMapping("/export")
    public void exportUserLoginLog(@RequestBody @Valid UserLoginLogPageQueryDto dto, HttpServletResponse response) {
        byte[] fileBytes = userLoginLogService.exportUserLoginLog(dto);
        String fileName = "用户登录日志_" + DateUtils.formatDate(DateUtils.today()) + ".xlsx";
        fileDownloadService.writeExcel(response, fileBytes, fileName);
    }
}