package com.mms.common.security.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 实现功能【JWT 配置属性】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-04 15:46:51
 */
@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

	private String secret;

	/**
	 * Access Token过期时间（毫秒），默认15分钟
	 */
	private Long accessExpiration = 900000L;

	/**
	 * Refresh Token过期时间（毫秒），默认7天
	 */
	private Long refreshExpiration = 604800000L;
}


