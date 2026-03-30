package com.mms.common.security.servlet.utils;

import com.mms.common.security.servlet.properties.CookieProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

/**
 * 实现功能【Cookie 工具类】
 *
 * @author li.hongyu
 * @date 2026-03-16 15:46:25
 */
public class CookieUtils {

    private CookieUtils() {
    }

    public static String getCookieValue(HttpServletRequest request, String cookieName) {
        if (request == null || cookieName == null || cookieName.isBlank()) {
            return null;
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * 写入 RefreshToken Cookie（使用 ResponseCookie 以支持 SameSite 等属性）
     */
    public static void writeRefreshTokenCookie(HttpServletResponse response, CookieProperties.RefreshToken props, String refreshToken, Long expiresInSeconds) {
        if (response == null || props == null || refreshToken == null || expiresInSeconds == null) {
            return;
        }
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(props.getName(), refreshToken)
                .path("/api" + props.getPath())
                .httpOnly(props.isHttpOnly())
                .secure(props.isSecure())
                .maxAge(Duration.ofSeconds(expiresInSeconds));

        if (props.getDomain() != null && !props.getDomain().isBlank()) {
            builder.domain(props.getDomain());
        }
        if (props.getSameSite() != null && !props.getSameSite().isBlank()) {
            builder.sameSite(props.getSameSite());
        }

        response.addHeader(HttpHeaders.SET_COOKIE, builder.build().toString());
    }

    /**
     * 清除 RefreshToken Cookie
     */
    public static void clearRefreshTokenCookie(HttpServletResponse response, CookieProperties.RefreshToken props) {
        if (response == null || props == null) {
            return;
        }
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(props.getName(), "")
                .path(props.getPath())
                .httpOnly(props.isHttpOnly())
                .secure(props.isSecure())
                .maxAge(Duration.ZERO);

        if (props.getDomain() != null && !props.getDomain().isBlank()) {
            builder.domain(props.getDomain());
        }
        if (props.getSameSite() != null && !props.getSameSite().isBlank()) {
            builder.sameSite(props.getSameSite());
        }

        response.addHeader(HttpHeaders.SET_COOKIE, builder.build().toString());
    }
}