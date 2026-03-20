package com.mms.common.security.core.utils;

import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.utils.IdUtils;
import com.mms.common.core.enums.jwt.TokenType;
import com.mms.common.security.core.constants.JwtClaimsConstants;
import com.mms.common.security.core.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 实现功能【JWT 工具类：生成、解析、验证JWT】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-04 15:46:51
 */
@AllArgsConstructor
public class JwtUtils {

	private final JwtProperties jwtProperties;

	/**
	 * 生成Access Token
	 *
	 * @param userId   用户ID
	 * @param username 用户名
	 * @param sessionId 会话ID
	 * @return Access Token
	 */
	public String generateAccessToken(Long userId, String username, String sessionId) {
		return generateToken(userId, username, sessionId, TokenType.ACCESS, jwtProperties.getAccessExpiration());
	}

	/**
	 * 生成Refresh Token
	 *
	 * @param userId   用户ID
	 * @param username 用户名
	 * @param sessionId 会话ID
	 * @return Refresh Token
	 */
	public String generateRefreshToken(Long userId, String username, String sessionId) {
		return generateToken(userId, username, sessionId, TokenType.REFRESH, jwtProperties.getRefreshExpiration());
	}

	private String generateToken(Long userId, String username, String sessionId, TokenType tokenType, long expirationMs) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + expirationMs);
		String jti = IdUtils.uuid32();

		return Jwts.builder()
				.id(jti)
				.claim(JwtClaimsConstants.USER_ID, userId)
				.claim(JwtClaimsConstants.USERNAME, username)
				.claim(JwtClaimsConstants.SESSION_ID, sessionId)
				.claim(JwtClaimsConstants.TOKEN_TYPE, tokenType.name())
				.issuedAt(now)
				.expiration(expiryDate)
				.signWith(getSigningKey())
				.compact();
	}

	/**
	 * 解析 Token，返回 Claims
	 * @param token Token
	 * @return Claims
	 */
	public Claims parseToken(String token) {
		try {
			return Jwts.parser()
					.verifyWith(getSigningKey())
					.build()
					.parseSignedClaims(token)
					.getPayload();
		} catch (ExpiredJwtException e) {
			throw new BusinessException(ErrorCode.LOGIN_EXPIRED);
		} catch (JwtException e) {
			throw new BusinessException(ErrorCode.INVALID_TOKEN);
		}
	}

	/**
	 * 获取 JWT 密钥
	 * @return JWT 密钥
	 */
	private SecretKey getSigningKey() {
		byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	/**
	 * 从Claims中提取Token类型
	 *
	 * @param claims JWT Claims
	 * @return Token类型，如果不存在则返回null
	 */
	public TokenType extractTokenType(Claims claims) {
		Object tokenTypeObj = claims.get(JwtClaimsConstants.TOKEN_TYPE);
		if (tokenTypeObj == null) {
			return null;
		}
		try {
			return TokenType.valueOf(tokenTypeObj.toString());
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 获取Access Token的TTL（秒数）
	 *
	 * @return TTL（秒）
	 */
	public long getAccessTokenTtlSeconds() {
		return jwtProperties.getAccessExpiration() / 1000;
	}

	/**
	 * 获取Refresh Token的TTL（秒数）
	 *
	 * @return TTL（秒）
	 */
	public long getRefreshTokenTtlSeconds() {
		return jwtProperties.getRefreshExpiration() / 1000;
	}
}


