package com.mms.base.controller.system;

import com.mms.base.service.system.service.ScheduledService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/scheduled")
public class ScheduledController {

    @Resource
    private ScheduledService scheduledService;
}