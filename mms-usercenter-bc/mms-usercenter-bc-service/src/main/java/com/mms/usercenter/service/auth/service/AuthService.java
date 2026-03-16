package com.mms.usercenter.service.auth.service;

import com.mms.usercenter.common.auth.dto.LoginDto;
import com.mms.usercenter.common.auth.vo.LoginVo;
import com.mms.usercenter.common.auth.vo.UserDetailVo;

/**
 * 实现功能【用户认证服务】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-11-07 11:39:24
 */
public interface AuthService {

    /**
     * 用户登录
     * @param dto 用户名、密码
     * @return 访问令牌和刷新令牌
     */
    LoginVo login(LoginDto dto);

    /**
     * 刷新Token
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌和刷新令牌
     */
    LoginVo refreshToken(String refreshToken);

    /**
     * 用户登出
     * @param refreshToken 刷新令牌
     */
    void logout(String refreshToken);

    /**
     * 获取当前用户信息
     * @return 当前用户信息
     */
    UserDetailVo getCurrentUser();
}