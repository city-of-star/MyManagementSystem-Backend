package com.mms.usercenter.controller.audit;

import com.mms.usercenter.service.audit.service.UserLoginLogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
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
@RequestMapping("/userLoginLog")
public class UserLoginLogController {

    @Resource
    private UserLoginLogService userLoginLogService;



}