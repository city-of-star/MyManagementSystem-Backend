package com.mms.common.security.core.config;

import com.mms.common.cache.config.CacheAutoConfiguration;
import com.mms.common.security.core.properties.GatewaySignatureProperties;
import com.mms.common.security.core.properties.JwtProperties;
import com.mms.common.security.core.properties.WhitelistProperties;
import com.mms.common.security.core.utils.*;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

/**
 * 实现功能【安全组件自动装配配置】
 * <p>
 * 提供了以下工具
 * 1. JwtUtils：JWT 工具类
 * 2. ReactiveTokenValidatorUtils：Reactive 环境下的 Token 验证工具类
 * 3. TokenBlacklistUtils：Token 黑名单工具类
 * 4. RefreshTokenUtils：刷新 Token 工具类
 * 5. TokenValidatorUtils：Token 验证工具类
 * 6. GatewaySignatureVerificationService：网关签名验证服务
 * 7. ServiceWhitelistService：服务白名单服务
 * 8. FeignHeaderRelayInterceptor：Feign 请求头传递拦截器
 * 9. FeignLogger：Feign 日志记录器
 * 10. FeignLoggerLevel：Feign 日志级别	
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-04 15:46:51
 */
@Configuration
@AutoConfigureAfter({CacheAutoConfiguration.class, RedisAutoConfiguration.class}) // 先等待redis配置加载
@EnableConfigurationProperties({JwtProperties.class, GatewaySignatureProperties.class, WhitelistProperties.class})  // 在此类当中注入配置属性Bean
public class SecurityAutoConfiguration {

	/**
	 * 创建 JwtUtils Bean
	 * 只有当配置了 jwt.secret 属性的时候才创建
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = "jwt", name = "secret")
	public JwtUtils jwtUtils(JwtProperties jwtProperties) {
		return new JwtUtils(jwtProperties);
	}

	/**
	 * 创建 ReactiveTokenValidatorUtils Bean
	 * 只有当 JwtUtils、ReactiveStringRedisTemplate 存在时创建
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnBean({JwtUtils.class, ReactiveStringRedisTemplate.class})
	public ReactiveTokenValidatorUtils reactiveTokenValidatorUtils(JwtUtils jwtUtils, ReactiveStringRedisTemplate reactiveStringRedisTemplate) {
		return new ReactiveTokenValidatorUtils(jwtUtils, reactiveStringRedisTemplate);
	}

	/**
	 * 创建 TokenBlacklistUtils Bean
	 * 只有当存在 RedisTemplate 时创建
	 */
	@Bean
	@ConditionalOnBean(RedisTemplate.class)
	@ConditionalOnMissingBean
	public TokenBlacklistUtils tokenBlacklistUtils() {
		return new TokenBlacklistUtils();
	}

	/**
	 * 创建 RefreshTokenUtils Bean
	 * 只有当存在 RedisTemplate 时创建
	 */
	@Bean
	@ConditionalOnBean(RedisTemplate.class)
	@ConditionalOnMissingBean
	public RefreshTokenUtils refreshTokenUtils() {
		return new RefreshTokenUtils();
	}

	/**
	 * 创建 TokenValidatorUtils Bean
	 * 只有当 JwtUtils、TokenBlacklistUtils 存在时才创建
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnBean({JwtUtils.class, TokenBlacklistUtils.class})
	public TokenValidatorUtils tokenValidatorUtils(JwtUtils jwtUtils, TokenBlacklistUtils tokenBlacklistUtils) {
		return new TokenValidatorUtils(jwtUtils, tokenBlacklistUtils);
	}
}


