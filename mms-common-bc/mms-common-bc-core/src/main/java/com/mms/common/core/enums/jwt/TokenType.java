package com.mms.common.core.enums.jwt;

/**
 * 实现功能【Token类型枚举】
 * <p>
 * 用于区分Access Token和Refresh Token
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-05 09:41:42
 */
public enum TokenType {
    /**
     * 访问令牌，用于API请求认证，有效期短
     */
    ACCESS,
    
    /**
     * 刷新令牌，用于刷新Access Token，有效期长
     */
    REFRESH
}