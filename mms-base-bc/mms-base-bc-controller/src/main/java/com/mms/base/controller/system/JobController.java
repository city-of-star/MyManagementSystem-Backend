package com.mms.base.controller.system;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.system.dto.JobBatchDeleteDto;
import com.mms.base.common.system.dto.JobCreateDto;
import com.mms.base.common.system.dto.JobPageQueryDto;
import com.mms.base.common.system.dto.JobStatusSwitchDto;
import com.mms.base.common.system.dto.JobUpdateDto;
import com.mms.base.common.system.vo.JobVo;
import com.mms.base.service.system.service.JobService;
import com.mms.common.core.constants.usercenter.PermissionConstants;
import com.mms.common.core.response.Response;
import com.mms.common.security.annotations.RequiresPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 实现功能【定时任务服务管理 Controller】
 * <p>
 * 提供定时任务服务管理的REST API接口
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-11 10:09:06
 */
@Tag(name = "定时任务服务管理", description = "定时任务服务管理相关接口")
@RestController
@RequestMapping("/job")
public class JobController {

    @Resource
    private JobService jobService;

    @Operation(summary = "分页查询定时任务列表", description = "根据条件分页查询定时任务定义列表")
    @RequiresPermission(PermissionConstants.SYSTEM_JOB_VIEW)
    @PostMapping("/page")
    public Response<Page<JobVo>> getJobPage(@RequestBody @Valid JobPageQueryDto dto) {
        return Response.success(jobService.getJobPage(dto));
    }

    @Operation(summary = "根据ID查询定时任务详情", description = "根据任务ID查询定时任务定义详细信息")
    @RequiresPermission(PermissionConstants.SYSTEM_JOB_VIEW)
    @GetMapping("/{jobId}")
    public Response<JobVo> getJobById(@PathVariable Long jobId) {
        return Response.success(jobService.getJobById(jobId));
    }

    @Operation(summary = "创建定时任务", description = "创建新的定时任务定义")
    @RequiresPermission(PermissionConstants.SYSTEM_JOB_CREATE)
    @PostMapping("/create")
    public Response<JobVo> createJob(@RequestBody @Valid JobCreateDto dto) {
        return Response.success(jobService.createJob(dto));
    }

    @Operation(summary = "更新定时任务信息", description = "更新定时任务定义的基本信息")
    @RequiresPermission(PermissionConstants.SYSTEM_JOB_UPDATE)
    @PutMapping("/update")
    public Response<JobVo> updateJob(@RequestBody @Valid JobUpdateDto dto) {
        return Response.success(jobService.updateJob(dto));
    }

    @Operation(summary = "删除定时任务", description = "逻辑删除定时任务定义（软删除）")
    @RequiresPermission(PermissionConstants.SYSTEM_JOB_DELETE)
    @DeleteMapping("/{jobId}")
    public Response<Void> deleteJob(@PathVariable Long jobId) {
        jobService.deleteJob(jobId);
        return Response.success();
    }

    @Operation(summary = "批量删除定时任务", description = "批量逻辑删除定时任务定义（软删除）")
    @RequiresPermission(PermissionConstants.SYSTEM_JOB_DELETE)
    @PostMapping("/batch-delete")
    public Response<Void> batchDeleteJob(@RequestBody @Valid JobBatchDeleteDto dto) {
        jobService.batchDeleteJob(dto);
        return Response.success();
    }

    @Operation(summary = "切换定时任务启用状态", description = "启用或禁用定时任务定义")
    @RequiresPermission(PermissionConstants.SYSTEM_JOB_UPDATE)
    @PostMapping("/switch-status")
    public Response<Void> switchJobStatus(@RequestBody @Valid JobStatusSwitchDto dto) {
        jobService.switchJobStatus(dto);
        return Response.success();
    }
}