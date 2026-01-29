package com.mms.usercenter.controller.auth;

import com.mms.common.core.response.Response;
import com.mms.usercenter.common.auth.vo.UserLoginLogVo;
import com.mms.usercenter.service.auth.service.LoginLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 实现功能【用户登录日志 Controller】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-01-29 17:16:02
 */
@Tag(name = "用户登录日志", description = "用户登录日志查询接口")
@RestController
@RequestMapping("/auth/login-log")
public class LoginLogController {

    @Resource
    private LoginLogService loginLogService;

    @Operation(summary = "查询最近的登录失败记录", description = "用于首页展示最近登录告警记录")
    @GetMapping("/recent-failed")
    public Response<List<UserLoginLogVo>> getRecentFailedLogs(
            @RequestParam(name = "limit", defaultValue = "5") int limit) {
        return Response.success(loginLogService.getRecentFailedLogs(limit));
    }
}

