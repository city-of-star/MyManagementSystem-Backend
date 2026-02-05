package com.mms.usercenter.controller.security;

import com.mms.common.core.response.Response;
import com.mms.usercenter.common.auth.vo.UserDetailVo;
import com.mms.usercenter.service.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 实现功能【用户详细信息 Controller】
 * <p>
 * 内部接口：提供用户详细信息查询
 * </p>
 *
 * @author li.hongyu
 * @date 2025-01-04 17:17:05
 */
@RestController
@RequestMapping("/info")
@RequiredArgsConstructor
@Tag(name = "内部接口-用户信息", description = "提供用户详细信息查询")
public class UserInfoController {

    private final UserService userService;

    @Operation(summary = "根据用户名获取用户详细信息", description = "内部接口")
    @GetMapping("/username/{username}")
    public Response<UserDetailVo> getUserByUsername(@PathVariable String username) {
        return Response.success(userService.getUserByUsername(username));
    }

    @Operation(summary = "根据用户ID获取用户详细信息", description = "内部接口")
    @GetMapping("/{userId}")
    public Response<UserDetailVo> getUserById(@PathVariable Long userId) {
        return Response.success(userService.getUserById(userId));
    }
}

