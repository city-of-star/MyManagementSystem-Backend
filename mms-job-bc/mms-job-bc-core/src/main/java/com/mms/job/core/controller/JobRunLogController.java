package com.mms.job.core.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.core.constants.usercenter.PermissionConstants;
import com.mms.common.core.response.Response;
import com.mms.common.security.servlet.annotations.RequiresPermission;
import com.mms.job.common.dto.JobRunLogBatchDeleteDto;
import com.mms.job.common.dto.JobRunLogPageQueryDto;
import com.mms.job.common.entity.JobRunLogEntity;
import com.mms.job.core.service.JobRunLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 实现功能【定时任务执行记录服务管理 Controller】
 * <p>
 * 提供定时任务执行记录管理的REST API接口
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-27 21:30:00
 */
@Tag(name = "定时任务执行记录管理", description = "定时任务执行记录管理相关接口")
@RestController
@RequestMapping("/job/run-log")
public class JobRunLogController {

    @Resource
    private JobRunLogService jobRunLogService;

    @Operation(summary = "分页查询定时任务执行记录列表", description = "根据条件分页查询定时任务执行记录列表")
    @RequiresPermission(PermissionConstants.JOB_RUN_LOG_VIEW)
    @PostMapping("/page")
    public Response<Page<JobRunLogEntity>> getJobRunLogPage(@RequestBody @Valid JobRunLogPageQueryDto dto) {
        return Response.success(jobRunLogService.getJobRunLogPage(dto));
    }

    @Operation(summary = "根据ID查询定时任务执行记录详情", description = "根据执行记录ID查询定时任务执行记录详情")
    @RequiresPermission(PermissionConstants.JOB_RUN_LOG_VIEW)
    @GetMapping("/{logId}")
    public Response<JobRunLogEntity> getJobRunLogById(@PathVariable Long logId) {
        return Response.success(jobRunLogService.getJobRunLogById(logId));
    }

    @Operation(summary = "删除定时任务执行记录", description = "根据ID删除单条定时任务执行记录")
    @RequiresPermission(PermissionConstants.JOB_RUN_LOG_DELETE)
    @DeleteMapping("/{logId}")
    public Response<Void> deleteJobRunLog(@PathVariable Long logId) {
        jobRunLogService.deleteJobRunLog(logId);
        return Response.success();
    }

    @Operation(summary = "批量删除定时任务执行记录", description = "批量删除定时任务执行记录")
    @RequiresPermission(PermissionConstants.JOB_RUN_LOG_DELETE)
    @PostMapping("/batch-delete")
    public Response<Void> batchDeleteJobRunLog(@RequestBody @Valid JobRunLogBatchDeleteDto dto) {
        jobRunLogService.batchDeleteJobRunLog(dto);
        return Response.success();
    }

    @Operation(summary = "导出定时任务执行记录", description = "导出定时任务执行记录（功能开发中）")
    @RequiresPermission(PermissionConstants.JOB_RUN_LOG_EXPORT)
    @PostMapping("/export")
    public Response<Void> exportJobRunLog(@RequestBody @Valid JobRunLogPageQueryDto dto) {
        jobRunLogService.exportJobRunLog(dto);
        return Response.success();
    }

    @Operation(summary = "重试执行定时任务", description = "根据执行记录ID重试执行（功能开发中）")
    @RequiresPermission(PermissionConstants.JOB_RUN_LOG_RETRY)
    @PostMapping("/{logId}/retry")
    public Response<Void> retryJobRun(@PathVariable Long logId) {
        jobRunLogService.retryJobRun(logId);
        return Response.success();
    }

    @Operation(summary = "终止执行定时任务", description = "根据执行记录ID终止执行（功能开发中）")
    @RequiresPermission(PermissionConstants.JOB_RUN_LOG_TERMINATE)
    @PostMapping("/{logId}/terminate")
    public Response<Void> terminateJobRun(@PathVariable Long logId) {
        jobRunLogService.terminateJobRun(logId);
        return Response.success();
    }
}