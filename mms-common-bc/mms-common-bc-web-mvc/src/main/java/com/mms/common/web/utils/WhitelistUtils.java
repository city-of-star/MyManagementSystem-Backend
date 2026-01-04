package com.mms.common.web.utils;

import com.mms.common.security.properties.WhitelistProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 实现功能【业务服务白名单工具】
 * <p>
 * 支持白名单缓存，当 Nacos 配置更新时自动刷新缓存
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-30 16:44:56
 */
@Slf4j
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
     * 缓存的白名单模式数组（使用 AtomicReference 保证线程安全）
     */
    private final AtomicReference<String[]> cachedPatterns = new AtomicReference<>();

    /**
     * 获取当前服务的白名单数组
     * <p>
     * 使用双重检查锁定
     * 场景1 = 99%的情况都是直接返回缓存，无锁开销 || 只有1%的情况（初始化时）需要同步，所以无需每次请求都加锁
     * 场景2 = 两个线程同时调用，都发现缓存为空，线程A进入 synchronized，线程B在 synchronized 外等待
     *        线程A初始化完成，释放锁，线程B获得锁，再次检查发现缓存已有，直接返回
     * </p>
     */
    public String[] getWhitelistPatterns() {
        String[] patterns = cachedPatterns.get();
        if (patterns == null) {
            synchronized (this) {
                patterns = cachedPatterns.get();
                if (patterns == null) {
                    patterns = buildWhitelistPatterns();
                    cachedPatterns.set(patterns);
                    log.debug("白名单缓存已初始化，共 {} 个模式", patterns.length);
                }
            }
        }
        return patterns;
    }

    /**
     * 构建白名单模式数组
     */
    private String[] buildWhitelistPatterns() {
        // 公共白名单
        List<String> patterns = new ArrayList<>(whitelistProperties.getCommon());
        // 服务专属白名单
        if ("usercenter".equals(applicationName)) {
            patterns.addAll(whitelistProperties.getUsercenter());
        } else if ("base".equals(applicationName)) {
            patterns.addAll(whitelistProperties.getBase());
        }
        return patterns.toArray(String[]::new);
    }

    /**
     * 监听配置变更事件，当白名单配置更新时刷新缓存
     * <p>
     * 当 Nacos 配置更新时，Spring Cloud 会触发 EnvironmentChangeEvent
     * </p>
     */
    @EventListener
    public void handleEnvironmentChange(EnvironmentChangeEvent event) {
        Set<String> keys = event.getKeys();
        // 检查是否有白名单相关的配置变更
        boolean whitelistChanged = keys.stream()
                .anyMatch(key -> key.startsWith("whitelist."));
        
        if (whitelistChanged) {
            synchronized (this) {
                cachedPatterns.set(null);
                log.info("检测到白名单配置变更，已清除缓存。变更的配置键: {}", keys);
            }
        }
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

        for (String pattern : getWhitelistPatterns()) {
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