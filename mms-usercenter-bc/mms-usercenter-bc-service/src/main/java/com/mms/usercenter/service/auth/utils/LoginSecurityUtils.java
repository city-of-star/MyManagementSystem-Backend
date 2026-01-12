package com.mms.usercenter.service.auth.utils;

import com.mms.usercenter.common.security.constants.LoginSecurityConstants;
import com.mms.usercenter.common.security.properties.LoginSecurityProperties;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 实现功能【登录安全工具类】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-11-08 16:59:52
 */
@Component
public class LoginSecurityUtils {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private LoginSecurityProperties securityProperties;

    /**
     * 增加登录失败次数
     */
    public void incrementLoginAttempts(String username) {
        String key = LoginSecurityConstants.LOGIN_ATTEMPT_PREFIX + username;
        redisTemplate.opsForValue().increment(key, 1);
        // 设置过期时间24h，避免永久存储
        redisTemplate.expire(key, 24, TimeUnit.HOURS);
    }

    /**
     * 获取登录失败次数
     */
    public int getLoginAttempts(String username) {
        String key = LoginSecurityConstants.LOGIN_ATTEMPT_PREFIX + username;
        Object attempts = redisTemplate.opsForValue().get(key);
        return attempts == null ? 0 : Integer.parseInt(attempts.toString());
    }

    /**
     * 重置登录失败次数
     */
    public void resetLoginAttempts(String username) {
        String key = LoginSecurityConstants.LOGIN_ATTEMPT_PREFIX + username;
        redisTemplate.delete(key);
    }

    /**
     * 锁定账号
     */
    public void lockAccount(String username) {
        String lockKey = LoginSecurityConstants.ACCOUNT_LOCK_PREFIX + username;
        redisTemplate.opsForValue().set(lockKey, "locked",
                securityProperties.getLockTime(), TimeUnit.MINUTES);

        // 清空失败次数
        resetLoginAttempts(username);
    }

    /**
     * 检查账号是否被锁定
     */
    public boolean isAccountLocked(String username) {
        String lockKey = LoginSecurityConstants.ACCOUNT_LOCK_PREFIX + username;
        return Boolean.TRUE.equals(redisTemplate.hasKey(lockKey));
    }

    /**
     * 获取剩余锁定时间
     */
    public long getLockRemainingTime(String username) {
        String lockKey = LoginSecurityConstants.ACCOUNT_LOCK_PREFIX + username;
        return redisTemplate.getExpire(lockKey, TimeUnit.SECONDS);
    }

    /**
     * 删除锁定状态
     */
    public void clearAccountLock(String username) {
        String lockKey = LoginSecurityConstants.ACCOUNT_LOCK_PREFIX + username;
        redisTemplate.delete(lockKey);
    }
}

