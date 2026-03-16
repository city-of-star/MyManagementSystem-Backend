package com.mms.common.security.servlet.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 实现功能【Cookie 配置属性】
 *
 * @author li.hongyu
 * @date 2026-03-16 15:46:36
 */
@Data
@ConfigurationProperties(prefix = "security.cookie")
public class CookieProperties {

    /**
     * Refresh Token Cookie 配置
     */
    private RefreshToken refreshToken = new RefreshToken();

    @Data
    public static class RefreshToken {
        /**
         * Cookie 名称
         */
        private String name = "refreshToken";

        /**
         * Cookie Path（按网关路由调整）
         */
        private String path = "/usercenter/auth";

        /**
         * 是否 HttpOnly（默认 true，前端 JS 不可读）
         */
        private boolean httpOnly = true;

        /**
         * 是否 Secure（建议生产环境 HTTPS 开启）
         */
        private boolean secure = false;

        /**
         * SameSite 策略：Strict/Lax/None（为空则不设置）
         */
        private String sameSite = "Lax";

        /**
         * Domain（为空则不设置）
         */
        private String domain;
    }
}