package com.mms.job.core.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.core.constants.usercenter.PermissionConstants;
import com.mms.common.core.response.Response;
import com.mms.common.security.annotations.RequiresPermission;
import com.mms.job.common.dto.JobRunLogPageQueryDto;
import com.mms.job.common.entity.JobRunLogEntity;
import com.mms.job.core.service.JobRunLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @RequiresPermission(PermissionConstants.SYSTEM_JOB_VIEW)
    @PostMapping("/page")
    public Response<Page<JobRunLogEntity>> getJobRunLogPage(@RequestBody @Valid JobRunLogPageQueryDto dto) {
        return Response.success(jobRunLogService.getJobRunLogPage(dto));
    }
}