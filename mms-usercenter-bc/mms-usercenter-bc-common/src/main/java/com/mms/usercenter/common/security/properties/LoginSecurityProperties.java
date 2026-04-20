package com.mms.usercenter.common.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 实现功能【登录安全配置属性】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-11-08 16:55:50
 */
@Data
@Component
@ConfigurationProperties(prefix = "login.security")
public class LoginSecurityProperties {

    /**
     * 最大登录失败次数
     */
    private Integer maxAttempts = 5;

    /**
     * 连续登录失败时间窗口（分钟）
     */
    private Integer attemptWindow = 30;

    /**
     * 锁定时间（分钟）
     */
    private Integer lockTime = 30;

    /**
     * 默认密码前缀
     */
    private String defaultPasswordPrefix = "MMS2025_";
}

