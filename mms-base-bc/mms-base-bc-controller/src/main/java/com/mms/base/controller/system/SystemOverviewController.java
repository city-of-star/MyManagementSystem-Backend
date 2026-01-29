package com.mms.base.controller.system;

import com.mms.base.common.system.vo.SystemOverviewVo;
import com.mms.base.service.system.service.SystemOverviewService;
import com.mms.common.core.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 实现功能【系统运行总览 Controller】
 * <p>
 * 提供首页展示的系统运行状态等概要信息
 * <p>
 *
 * @author li.hongyu
 * @date 2026-01-29 17:02:33
 */
@Tag(name = "系统运行总览", description = "系统运行状态、资源使用等概要信息接口")
@RestController
@RequestMapping("/system")
public class SystemOverviewController {

    @Resource
    private SystemOverviewService systemOverviewService;

    @Operation(summary = "获取系统运行总览信息")
    @GetMapping("/overview")
    public Response<SystemOverviewVo> getOverview() {
        return Response.success(systemOverviewService.getOverview());
    }
}

