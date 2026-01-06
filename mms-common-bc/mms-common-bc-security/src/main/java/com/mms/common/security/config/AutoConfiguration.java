package com.mms.common.security.config;

import com.mms.common.security.properties.GatewaySignatureProperties;
import com.mms.common.security.properties.JwtProperties;
import com.mms.common.security.properties.WhitelistProperties;
import com.mms.common.security.service.GatewaySignatureVerificationService;
import com.mms.common.security.utils.*;
import com.mms.common.security.service.ServiceWhitelistService;
import jakarta.servlet.http.HttpServletRequest;
import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

/**
 * 实现功能【安全组件自动装配配置】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-04 15:46:51
 */
@Configuration
@EnableConfigurationProperties({JwtProperties.class, GatewaySignatureProperties.class, WhitelistProperties.class})  // 在此类当中注入配置属性Bean
public class AutoConfiguration {

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
	 * 仅当 JwtUtils、ReactiveStringRedisTemplate 存在时创建
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnBean({JwtUtils.class, ReactiveStringRedisTemplate.class})
	public ReactiveTokenValidatorUtils reactiveTokenValidatorUtils(
			JwtUtils jwtUtils,
			ReactiveStringRedisTemplate reactiveStringRedisTemplate) {
		return new ReactiveTokenValidatorUtils(jwtUtils, reactiveStringRedisTemplate);
	}

	/**
	 * 创建 TokenBlacklistUtils Bean
	 * 仅在非Reactive环境下（存在 RedisTemplate 且不存在 ReactiveStringRedisTemplate）创建
	 */
	@Bean
	@ConditionalOnBean(RedisTemplate.class)
	@ConditionalOnMissingBean(ReactiveStringRedisTemplate.class)
	public TokenBlacklistUtils tokenBlacklistUtils(RedisTemplate<String, Object> redisTemplate) {
		return new TokenBlacklistUtils(redisTemplate);
	}

	/**
	 * 创建 RefreshTokenUtils Bean
	 * 仅在非Reactive环境下（存在 RedisTemplate 且不存在 ReactiveStringRedisTemplate）创建
	 */
	@Bean
	@ConditionalOnBean(RedisTemplate.class)
	@ConditionalOnMissingBean(ReactiveStringRedisTemplate.class)
	public RefreshTokenUtils refreshTokenUtils(RedisTemplate<String, Object> redisTemplate) {
		return new RefreshTokenUtils(redisTemplate);
	}

	/**
	 * 创建 TokenValidatorUtils Bean
	 * 只有当 JwtUtils 存在时才创建（即配置了 jwt.secret）
	 * 只有当 TokenBlacklistUtils 存在时才创建（即配置了 RedisTemplate）
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnBean({JwtUtils.class, TokenBlacklistUtils.class})
	public TokenValidatorUtils tokenValidatorUtils(
			JwtUtils jwtUtils,
			TokenBlacklistUtils tokenBlacklistUtils) {
		return new TokenValidatorUtils(jwtUtils, tokenBlacklistUtils);
	}

	/**
	 * 创建 GatewaySignatureVerificationService Bean
	 * 仅在 Servlet 环境（业务服务）中创建，网关（WebFlux）环境不创建
	 * 需要配置 gateway.signature.public-key 属性
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnClass(HttpServletRequest.class)
	@ConditionalOnProperty(prefix = "gateway.signature", name = "public-key")
	public GatewaySignatureVerificationService gatewaySignatureVerificationService(
			GatewaySignatureProperties gatewaySignatureProperties) {
		return new GatewaySignatureVerificationService(gatewaySignatureProperties);
	}

	/**
	 * 创建 ServiceWhitelistService Bean
	 * 仅在 Servlet 环境（业务服务）中创建，网关（WebFlux）环境不创建
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnClass(HttpServletRequest.class)
	public ServiceWhitelistService whitelistUtils(WhitelistProperties whitelistProperties) {
		return new ServiceWhitelistService(whitelistProperties);
	}

	/**
	 * Feign 相关 Bean：仅在 Servlet 环境且存在 Feign 依赖时装配，
	 * 避免网关（无 Feign 依赖）启动时报缺类。
	 */
	@Configuration
	@ConditionalOnClass({jakarta.servlet.http.HttpServletRequest.class, feign.RequestInterceptor.class})
	static class FeignBeans {

		@Bean
		@ConditionalOnMissingBean(RequestInterceptor.class)
		public RequestInterceptor feignHeaderRelayInterceptor() {
			return new com.mms.common.security.feign.FeignHeaderRelayInterceptor();
		}

		@Bean
		@ConditionalOnMissingBean(Logger.class)
		public Logger feignLogger() {
			return new com.mms.common.security.feign.FeignLogger().feignLogger();
		}

		@Bean
		@ConditionalOnMissingBean(Logger.Level.class)
		public Logger.Level feignLoggerLevel() {
			return Logger.Level.FULL;
		}
	}
}


