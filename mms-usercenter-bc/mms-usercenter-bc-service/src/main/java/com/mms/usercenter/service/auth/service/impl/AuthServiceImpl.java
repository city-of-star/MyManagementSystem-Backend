package com.mms.usercenter.service.auth.service.impl;

import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import com.mms.common.security.constants.JwtConstants;
import com.mms.common.security.utils.RefreshTokenUtils;
import com.mms.common.security.utils.TokenBlacklistUtils;
import com.mms.common.security.utils.JwtUtils;
import com.mms.common.core.enums.jwt.TokenType;
import com.mms.common.security.utils.TokenValidatorUtils;
import com.mms.common.webmvc.utils.UserContextUtils;
import com.mms.usercenter.common.auth.dto.LoginDto;
import com.mms.usercenter.common.auth.dto.LogoutDto;
import com.mms.usercenter.common.auth.dto.RefreshTokenDto;
import com.mms.usercenter.common.auth.entity.UserEntity;
import com.mms.usercenter.common.auth.vo.LoginVo;
import com.mms.usercenter.common.auth.vo.UserDetailVo;
import com.mms.usercenter.common.security.properties.LoginSecurityProperties;
import com.mms.usercenter.service.auth.service.UserService;
import com.mms.usercenter.service.auth.utils.LoginSecurityUtils;
import com.mms.usercenter.common.auth.entity.UserLoginLogEntity;
import com.mms.usercenter.service.auth.mapper.UserMapper;
import com.mms.usercenter.service.auth.mapper.UserLoginLogMapper;
import com.mms.usercenter.service.auth.service.AuthService;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 实现功能【用户认证服务实现类】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-11-07 11:39:50
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private JwtUtils jwtUtils;

    @Resource
    private TokenValidatorUtils tokenValidatorUtils;

    @Resource
    private TokenBlacklistUtils tokenBlacklistUtils;

    @Resource
    private RefreshTokenUtils refreshTokenUtils;

    @Resource
    private LoginSecurityUtils loginSecurityUtils;

    @Resource
    private LoginSecurityProperties loginSecurityProperties;

    @Resource
    private UserLoginLogMapper userLoginLogMapper;

    @Resource
    private UserService userService;

    @Override
    public LoginVo login(LoginDto dto) {
        try {
            // 查询用户
            UserEntity user = userMapper.selectByUsername(dto.getUsername());

            // 验证用户是否存在
            if (user == null) {
                // 用户名不存在时，直接抛出异常，不增加失败次数（避免用户名枚举攻击）
                throw new BusinessException(ErrorCode.LOGIN_FAILED);
            }

            // 检查账号是否被临时锁定（登录失败次数过多导致的锁定）
            if (loginSecurityUtils.isAccountLocked(user.getUsername())) {
                long remainingTime = loginSecurityUtils.getLockRemainingTime(user.getUsername());
                recordLoginLog(user.getId(), user.getUsername(), 0, "账号已被临时锁定");
                throw new BusinessException(ErrorCode.ACCOUNT_LOCKED,
                        String.format("账号已被锁定，请在 %d 分钟后重试", remainingTime / 60));
            }

            // 验证账号状态
            if (user.getStatus() == 0) {
                recordLoginLog(user.getId(), user.getUsername(), 0, "账号已禁用");
                throw new BusinessException(ErrorCode.ACCOUNT_DISABLED);
            }

            // 验证账号是否锁定（数据库中的锁定状态）
            if (user.getLocked() == 1) {
                recordLoginLog(user.getId(), user.getUsername(), 0, "账号已锁定");
                throw new BusinessException(ErrorCode.ACCOUNT_LOCKED);
            }

            // 验证密码
            if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
                handleLoginFailure(dto.getUsername(), user, "密码错误");
                throw new BusinessException(ErrorCode.LOGIN_FAILED);
            }

            // 登录成功，重置失败次数
            loginSecurityUtils.resetLoginAttempts(dto.getUsername());

            // 更新最后登录时间和IP
            user.setLastLoginTime(LocalDateTime.now());
            String clientIp = UserContextUtils.getClientIp();
            user.setLastLoginIp(StringUtils.hasText(clientIp) ? clientIp : "unknown");
            userMapper.updateById(user);

            // 生成双 Token
            String accessToken = jwtUtils.generateAccessToken(user.getId(), dto.getUsername());
            String refreshToken = jwtUtils.generateRefreshToken(user.getId(), dto.getUsername());

            // 将 Refresh Token 存储到 Redis
            Claims refreshClaims = jwtUtils.parseToken(refreshToken);
            refreshTokenUtils.storeRefreshToken(dto.getUsername(), refreshClaims);

            // 记录登录成功日志
            recordLoginLog(user.getId(), user.getUsername(), 1, "登录成功");

            // 构建 LoginVo
            return buildLoginVo(accessToken, refreshToken);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new ServerException("登录失败", e);
        }
    }

    @Override
    public LoginVo refreshToken(RefreshTokenDto dto) {
        // 解析并验证Refresh Token
        Claims refreshClaims = tokenValidatorUtils.parseAndValidate(dto.getRefreshToken(), TokenType.REFRESH);

        // 提取用户名、用户ID
        String username = Optional.ofNullable(refreshClaims.get(JwtConstants.Claims.USERNAME))
                .map(Object::toString)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));
        Long userId = Optional.ofNullable(refreshClaims.get(JwtConstants.Claims.USER_ID))
                .map(Object::toString)
                .filter(StringUtils::hasText)
                .map(Long::valueOf)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));

        // 验证Refresh Token是否在Redis中存在且有效
        if (!refreshTokenUtils.isRefreshTokenValid(username, refreshClaims)) {
            throw new BusinessException(ErrorCode.LOGIN_EXPIRED);
        }

        // 将旧的Refresh Token加入黑名单
        tokenBlacklistUtils.addToBlacklist(refreshClaims);

        // 生成新的双Token
        String newAccessToken = jwtUtils.generateAccessToken(userId, username);
        String newRefreshToken = jwtUtils.generateRefreshToken(userId, username);

        // 将新的Refresh Token存储到Redis（替换旧的）
        Claims newRefreshClaims = jwtUtils.parseToken(newRefreshToken);
        refreshTokenUtils.storeRefreshToken(username, newRefreshClaims);

        return buildLoginVo(newAccessToken, newRefreshToken);
    }

    @Override
    public void logout(LogoutDto dto) {
        // 从请求上下文获取 Access Token 信息（网关已验证并透传）
        String accessTokenJti = UserContextUtils.getTokenJti();
        String accessTokenExp = UserContextUtils.getTokenExp();

        // 将Access Token加入黑名单
        if (StringUtils.hasText(accessTokenJti) && StringUtils.hasText(accessTokenExp)) {
            try {
                long expirationTime = Long.parseLong(accessTokenExp);
                tokenBlacklistUtils.addToBlacklist(accessTokenJti, expirationTime, TokenType.ACCESS);
            } catch (NumberFormatException e) {
                // 过期时间格式错误，忽略Access Token黑名单操作
            }
        }

        // 解析并验证Refresh Token
        Claims refreshClaims = tokenValidatorUtils.parseAndValidate(dto.getRefreshToken(), TokenType.REFRESH);
        // 将Refresh Token加入黑名单
        tokenBlacklistUtils.addToBlacklist(refreshClaims);

        // 从Token中获取用户名，如果存在则从Redis删除对应的Refresh Token
        // 实现单点登录控制，确保旧Refresh Token立即失效
        Optional.ofNullable(refreshClaims.get(JwtConstants.Claims.USERNAME))
                .map(Object::toString)
                .ifPresent(username -> refreshTokenUtils.removeRefreshToken(username));
    }

    @Override
    public UserDetailVo getCurrentUser() {
        // 从请求上下文获取用户名（网关已验证并透传）
        Long userId = UserContextUtils.getUserId();

        // 检查是否为空
        if (userId == null) {
            throw new BusinessException(ErrorCode.LOGIN_EXPIRED);
        }

        // 查询信息
        return userService.getUserById(userId);
    }

    /**
     * 处理登录失败逻辑
     */
    private void handleLoginFailure(String username, UserEntity user, String failureReason) {
        // 记录登录失败日志
        Long userId = user != null ? user.getId() : null;
        recordLoginLog(userId, username, 0, failureReason);

        // 增加失败次数
        loginSecurityUtils.incrementLoginAttempts(username);

        // 获取失败次数
        int attempts = loginSecurityUtils.getLoginAttempts(username);

        // 如果达到最大尝试次数，锁定账号
        if (attempts >= loginSecurityProperties.getMaxAttempts()) {

            // 锁定账号
            loginSecurityUtils.lockAccount(username);

            throw new BusinessException(ErrorCode.ACCOUNT_LOCKED,
                    String.format("登录失败次数过多，账号已被锁定 %d 分钟", loginSecurityProperties.getLockTime()));
        }

        // 获取剩余尝试次数
        int remainingAttempts = loginSecurityProperties.getMaxAttempts() - attempts;

        // 提示剩余尝试次数
        throw new BusinessException(ErrorCode.LOGIN_FAILED,
                String.format("用户名或密码错误，您还有 %d 次尝试机会", remainingAttempts));
    }

    /**
     * 解除因密码输入错误次数过多导致的临时锁定账号（管理员使用）
     */
    public void unlockAccount(String username) {

        // 重置登录失败次数
        loginSecurityUtils.resetLoginAttempts(username);

        // 删除锁定状态
        loginSecurityUtils.clearAccountLock(username);
    }

    /**
     * 构建LoginVo对象
     */
    private LoginVo buildLoginVo(String accessToken, String refreshToken) {
        LoginVo loginVo = new LoginVo();
        loginVo.setAccessToken(accessToken);
        loginVo.setRefreshToken(refreshToken);
        loginVo.setAccessTokenExpiresIn(jwtUtils.getAccessTokenTtlSeconds());
        loginVo.setRefreshTokenExpiresIn(jwtUtils.getRefreshTokenTtlSeconds());
        return loginVo;
    }

    /**
     * 记录登录日志
     *
     * @param userId      用户ID（可能为null，如用户不存在时）
     * @param username    用户名
     * @param loginStatus 登录状态：0-失败，1-成功
     * @param message     登录消息（失败原因等）
     */
    private void recordLoginLog(Long userId, String username, Integer loginStatus, String message) {
        try {
            String clientIp = UserContextUtils.getClientIp();
            String userAgent = UserContextUtils.getUserAgent();
            String loginLocation = UserContextUtils.getLoginLocation();

            UserLoginLogEntity logEntity = new UserLoginLogEntity();
            logEntity.setUserId(userId);
            logEntity.setUsername(username);
            logEntity.setLoginType("password"); // 密码登录
            logEntity.setLoginIp(StringUtils.hasText(clientIp) ? clientIp : "unknown");
            logEntity.setLoginLocation(StringUtils.hasText(loginLocation) ? loginLocation : "unknown");
            logEntity.setUserAgent(StringUtils.hasText(userAgent) ? userAgent : "unknown");
            logEntity.setLoginStatus(loginStatus);
            logEntity.setLoginMessage(message);
            logEntity.setLoginTime(LocalDateTime.now());

            userLoginLogMapper.insert(logEntity);
        } catch (Exception e) {
            // 记录日志失败不应该影响登录流程，只记录异常
            // 可以使用日志框架记录，这里暂时忽略
        }
    }

    /**
     * 将 UserEntity 转换为 UserDetailVo
     *
     * @param entity 用户实体
     * @return 用户VO
     */
    private UserDetailVo convertToVo(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        UserDetailVo vo = new UserDetailVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}