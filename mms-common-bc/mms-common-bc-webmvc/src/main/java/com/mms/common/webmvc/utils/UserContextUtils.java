package com.mms.common.webmvc.utils;

import com.mms.common.core.constants.gateway.GatewayConstants;
import com.mms.common.core.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 实现功能【用户上下文工具类】
 * <p>
 * 从请求头中提取用户信息，封装为 UserContext
 * 网关会将用户信息放入请求头，下游服务通过此工具类获取
 * 注意：
 * - 无参数方法会自动从当前请求线程获取请求对象，仅在 Spring MVC 请求线程中可用
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-02 11:15:16
 */
public class UserContextUtils {

    /**
     * 获取当前请求的用户上下文（自动从请求线程获取）
     * <p>
     * 推荐使用此方法，无需传递 HttpServletRequest 参数
     * </p>
     *
     * @return 用户上下文，如果不在请求线程中或请求头中没有用户信息则返回null
     */
    public static UserContext getUserContext() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return null;
        }
        return getUserContext(request);
    }

    /**
     * 从指定请求中获取用户上下文
     * <p>
     * 用于特殊场景，如测试或需要从特定请求获取信息
     * </p>
     *
     * @param request HTTP请求对象
     * @return 用户上下文，如果请求头中没有用户信息则返回null
     */
    public static UserContext getUserContext(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        String username = request.getHeader(GatewayConstants.Headers.USER_NAME);
        String userId = request.getHeader(GatewayConstants.Headers.USER_ID);
        String tokenJti = request.getHeader(GatewayConstants.Headers.TOKEN_JTI);
        String clientIp = request.getHeader(GatewayConstants.Headers.CLIENT_IP);
        String expiration = request.getHeader(GatewayConstants.Headers.TOKEN_EXP);
        String userAgent = request.getHeader(GatewayConstants.Headers.USER_AGENT);
        String loginLocation = request.getHeader(GatewayConstants.Headers.LOGIN_LOCATION);

        // 创建 userContext 实体
        UserContext userContext = new UserContext();
        userContext.setUsername(username);
        if (userId != null && !userId.isBlank()) {
            try {
                userContext.setUserId(Long.valueOf(userId));
            } catch (NumberFormatException ignored) {
                // 忽略格式错误的 userId
            }
        }
        userContext.setTokenJti(tokenJti);
        userContext.setClientIp(clientIp);
        userContext.setExpiration(expiration);
        userContext.setUserAgent(userAgent);
        userContext.setLoginLocation(loginLocation);

        return userContext;
    }

    /**
     * 获取当前请求对象
     * <p>
     * 从 RequestContextHolder 中获取当前请求，仅在 Spring MVC 请求线程中可用
     * </p>
     *
     * @return 当前请求对象，如果不在请求线程中则返回null
     */
    private static HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                return attributes.getRequest();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * 获取当前登录用户名（自动从请求线程获取）
     *
     * @return 用户名，如果不存在则返回null
     */
    public static String getUsername() {
        UserContext context = getUserContext();
        return context != null ? context.getUsername() : null;
    }

    /**
     * 获取当前登录用户ID（自动从请求线程获取）
     *
     * @return 用户ID，如果不存在则返回null
     */
    public static Long getUserId() {
        UserContext context = getUserContext();
        return context != null ? context.getUserId() : null;
    }

    /**
     * 获取当前登录 Token Jti（自动从请求线程获取）
     *
     * @return Token Jti，如果不存在则返回null
     */
    public static String getTokenJti() {
        UserContext context = getUserContext();
        return context != null ? context.getTokenJti() : null;
    }

    /**
     * 获取当前登录 Token 过期时间（自动从请求线程获取）
     *
     * @return Token 过期时间，如果不存在则返回null
     */
    public static String getTokenExp() {
        UserContext context = getUserContext();
        return context != null ? context.getExpiration() : null;
    }

    /**
     * 获取当前请求的客户端IP（自动从请求线程获取）
     *
     * @return 客户端IP，如果不存在则返回null
     */
    public static String getClientIp() {
        UserContext context = getUserContext();
        return context != null ? context.getClientIp() : null;
    }

    /**
     * 获取当前请求的用户代理（自动从请求线程获取）
     *
     * @return 用户代理，如果不存在则返回null
     */
    public static String getUserAgent() {
        UserContext context = getUserContext();
        return context != null ? context.getUserAgent() : null;
    }

    /**
     * 获取当前请求的登录地点（自动从请求线程获取）
     *
     * @return 登录地点，如果不存在则返回null
     */
    public static String getLoginLocation() {
        UserContext context = getUserContext();
        return context != null ? context.getLoginLocation() : null;
    }

    /**
     * 私有构造函数，防止实例化
     */
    private UserContextUtils() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }
}

