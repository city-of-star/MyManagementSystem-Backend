package com.mms.usercenter.controller.security;

import com.mms.common.core.constants.usercenter.PermissionConstants;
import com.mms.common.core.response.Response;
import com.mms.common.security.servlet.annotations.RequiresPermission;
import com.mms.usercenter.common.security.dto.OnlineUserForceLogoutDto;
import com.mms.usercenter.common.security.vo.OnlineUserVo;
import com.mms.usercenter.service.security.service.OnlineUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 实现功能【在线用户服务 Controller】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-04-03 14:55:10
 */
@Tag(name = "在线用户", description = "在线用户相关接口")
@RestController
@RequestMapping("/security/online-user")
@RequiredArgsConstructor
public class OnlineUserController {

    private final OnlineUserService onlineUserService;

    @Operation(summary = "查询在线用户列表")
    @RequiresPermission(PermissionConstants.SECURITY_ONLINE_USER_VIEW)
    @GetMapping("/list")
    public Response<List<OnlineUserVo>> getOnlineUsers() {
        return Response.success(onlineUserService.getOnlineUsers());
    }

    @Operation(summary = "强制用户下线")
    @RequiresPermission(PermissionConstants.SECURITY_ONLINE_USER_FORCE_LOGOUT)
    @PostMapping("/force-logout")
    public Response<Void> forceLogout(@RequestBody @Valid OnlineUserForceLogoutDto dto) {
        onlineUserService.forceLogout(dto.getUserId());
        return Response.success();
    }
}