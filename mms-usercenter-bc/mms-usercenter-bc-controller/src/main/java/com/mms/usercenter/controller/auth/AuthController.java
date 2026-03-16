package com.mms.usercenter.controller.auth;

import com.mms.common.core.response.Response;
import com.mms.common.security.servlet.properties.CookieProperties;
import com.mms.common.security.servlet.utils.CookieUtils;
import com.mms.usercenter.common.auth.dto.LoginDto;
import com.mms.usercenter.common.auth.vo.LoginVo;
import com.mms.usercenter.common.auth.vo.UserDetailVo;
import com.mms.usercenter.service.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 实现功能【用户认证服务 Controller】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-11-07 14:20:55
 */
@Tag(name = "用户认证", description = "用户认证相关接口")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private AuthService authService;

    @Resource
    private CookieProperties cookieProperties;

    @Operation(summary = "用户登录", description = "通过用户名和密码进行登录，返回【Access Token】和【Refresh Token】")
    @PostMapping("/login")
    public Response<LoginVo> login(@RequestBody @Valid LoginDto dto, HttpServletResponse response) {
        LoginVo loginVo = authService.login(dto);
        // 将 Refresh Token 写入 HttpOnly Cookie
        CookieUtils.writeRefreshTokenCookie(response, cookieProperties.getRefreshToken(), loginVo.getRefreshToken(), loginVo.getRefreshTokenExpiresIn());
        return Response.success(loginVo);
    }

    @Operation(summary = "刷新Token", description = "使用【Refresh Token】刷新【Access Token】和【Refresh Token】")
    @PostMapping("/refresh")
    public Response<LoginVo> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtils.getCookieValue(request, cookieProperties.getRefreshToken().getName());
        LoginVo loginVo = authService.refreshToken(refreshToken);
        // 轮换 Refresh Token，同步更新 HttpOnly Cookie
        CookieUtils.writeRefreshTokenCookie(response, cookieProperties.getRefreshToken(), loginVo.getRefreshToken(), loginVo.getRefreshTokenExpiresIn());
        return Response.success(loginVo);
    }

    @Operation(summary = "用户登出", description = "登出并让【Access Token】和【Refresh Token 失效】")
    @PostMapping("/logout")
    public Response<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtils.getCookieValue(request, cookieProperties.getRefreshToken().getName());
        authService.logout(refreshToken);
        // 删除 Refresh Token Cookie
        CookieUtils.clearRefreshTokenCookie(response, cookieProperties.getRefreshToken());
        return Response.success();
    }

    @Operation(summary = "获取当前用户信息", description = "获取当前用户信息")
    @PostMapping("/getCurrentUser")
    public Response<UserDetailVo> getCurrentUser() {
        return Response.success(authService.getCurrentUser());
    }
}