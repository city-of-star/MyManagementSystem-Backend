package com.mms.usercenter.service.auth.utils;

import com.mms.common.cache.utils.RedisUtils;
import com.mms.usercenter.common.security.constants.LoginSecurityConstants;
import com.mms.usercenter.common.security.properties.LoginSecurityProperties;
import jakarta.annotation.Resource;
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
    private LoginSecurityProperties securityProperties;

    /**
     * 增加登录失败次数
     */
    public void incrementLoginAttempts(String username) {
        String key = LoginSecurityConstants.LOGIN_ATTEMPT_PREFIX + username;
        RedisUtils.increment(key);
        RedisUtils.expire(key, securityProperties.getAttemptWindow(), TimeUnit.MINUTES);
    }

    /**
     * 获取登录失败次数
     */
    public int getLoginAttempts(String username) {
        String key = LoginSecurityConstants.LOGIN_ATTEMPT_PREFIX + username;
        Integer attempts = RedisUtils.get(key, Integer.class);
        return attempts == null ? 0 : attempts;
    }

    /**
     * 重置登录失败次数
     */
    public void resetLoginAttempts(String username) {
        String key = LoginSecurityConstants.LOGIN_ATTEMPT_PREFIX + username;
        RedisUtils.delete(key);
    }

    /**
     * 锁定账号
     */
    public void lockAccount(String username) {
        String lockKey = LoginSecurityConstants.ACCOUNT_LOCK_PREFIX + username;
        RedisUtils.set(lockKey, "登录失败次数过多", securityProperties.getLockTime(), TimeUnit.MINUTES);
        resetLoginAttempts(username);
    }

    /**
     * 检查账号是否被锁定
     */
    public boolean isAccountLocked(String username) {
        String lockKey = LoginSecurityConstants.ACCOUNT_LOCK_PREFIX + username;
        return Boolean.TRUE.equals(RedisUtils.exists(lockKey));
    }

    /**
     * 获取剩余锁定时间（秒）
     */
    public long getLockRemainingTime(String username) {
        String lockKey = LoginSecurityConstants.ACCOUNT_LOCK_PREFIX + username;
        Long expire = RedisUtils.getExpire(lockKey);
        return expire == null ? -2L : expire;
    }

    /**
     * 删除锁定状态
     */
    public void clearAccountLock(String username) {
        String lockKey = LoginSecurityConstants.ACCOUNT_LOCK_PREFIX + username;
        RedisUtils.delete(lockKey);
    }
}
