package com.mms.common.web.utils;

import com.mms.common.security.properties.WhitelistProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现功能【业务服务白名单工具】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-30 16:44:56
 */
@Component
@RequiredArgsConstructor
public class WhitelistUtils {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private final WhitelistProperties whitelistProperties;

    /**
     * 当前服务名
     */
    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * 当前服务的 context-path（如 /usercenter 或 /base），可能为空
     */
    @Value("${server.servlet.context-path:}")
    private String contextPath;

    /**
     * 获取当前服务的白名单数组，供 Spring Security 使用
     */
    public String[] getSecurityWhitelistPatterns() {
        List<String> patterns = new ArrayList<>();
        // 公共白名单
        patterns.addAll(whitelistProperties.getCommon());
        // 服务专属白名单
        if ("usercenter".equals(applicationName)) {
            patterns.addAll(whitelistProperties.getUsercenter());
        } else if ("base".equals(applicationName)) {
            patterns.addAll(whitelistProperties.getBase());
        }
        return patterns.toArray(String[]::new);
    }

    /**
     * 判断当前请求 URI 是否在白名单中（过滤器 / AOP 可用）
     * <p>
     * 会自动去掉 context-path，再与 whitelist.* 中的模式进行匹配。
     * </p>
     */
    public boolean isWhitelisted(HttpServletRequest request) {
        String uri = request.getRequestURI(); // 例如 /usercenter/doc.html 或 /doc.html
        String path = stripContextPath(uri);

        for (String pattern : getSecurityWhitelistPatterns()) {
            if (PATH_MATCHER.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 去掉 context-path，得到服务内部路径
     */
    private String stripContextPath(String uri) {
        if (contextPath == null || contextPath.isBlank()) {
            return uri;
        }
        return uri.startsWith(contextPath) ? uri.substring(contextPath.length()) : uri;
    }
}