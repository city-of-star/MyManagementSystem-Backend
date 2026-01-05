package com.mms.common.security.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 实现功能【白名单匹配器抽象基类】
 * <p>
 * 提供统一的缓存机制、配置变更监听和路径匹配逻辑
 * 子类只需实现如何构建白名单模式列表
 * </p>
 *
 * @author li.hongyu
 * @date 2025-01-XX
 */
@Slf4j
public abstract class AbstractWhitelistService {

    /**
     * 路径模式解析器（线程安全）
     */
    private static final PathPatternParser PATH_PATTERN_PARSER = new PathPatternParser();

    /**
     * 缓存的白名单模式列表（使用 AtomicReference 保证线程安全）
     */
    private final AtomicReference<List<PathPattern>> cachedPatterns = new AtomicReference<>();

    /**
     * 获取白名单模式列表
     */
    protected List<PathPattern> getWhitelistPatterns() {
        return cachedPatterns.get();
    }

    /**
     * 构建并编译白名单模式列表
     * <p>
     * 调用子类实现的 buildRawPatterns() 获取原始模式字符串列表，
     * 然后编译为 PathPattern 对象列表
     * </p>
     */
    private List<PathPattern> buildAndCompilePatterns() {
        List<String> rawPatterns = buildRawPatterns();
        List<PathPattern> compiled = new ArrayList<>(rawPatterns.size());
        
        for (String pattern : rawPatterns) {
            if (pattern == null || pattern.isBlank()) {
                continue;
            }
            // 规范化路径：确保以 / 开头
            String normalized = normalizePath(pattern);
            try {
                compiled.add(PATH_PATTERN_PARSER.parse(normalized));
            } catch (Exception e) {
                log.warn("白名单模式解析失败，跳过该模式: {} - {}", pattern, e.getMessage());
            }
        }
        
        return compiled;
    }

    /**
     * 构建原始白名单模式字符串列表
     * <p>
     * 子类需要实现此方法，返回需要匹配的路径模式字符串列表
     * </p>
     *
     * @return 白名单模式字符串列表
     */
    protected abstract List<String> buildRawPatterns();

    /**
     * 判断路径是否在白名单中
     *
     * @param path 待检查的路径
     * @return 如果路径在白名单中则返回 true
     */
    public boolean isWhitelisted(String path) {
        if (path == null || path.isBlank()) {
            return false;
        }

        // 规范化路径：确保以 / 开头
        String normalizedPath = normalizePath(path);
        PathContainer pathContainer = PathContainer.parsePath(normalizedPath);

        List<PathPattern> patterns = getWhitelistPatterns();
        if (patterns == null || patterns.isEmpty()) {
            return false;
        }

        for (PathPattern pattern : patterns) {
            if (pattern.matches(pathContainer)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 初始化时加载白名单缓存
     */
    @PostConstruct
    public void initWhitelistCache() {
        log.info("白名单配置开始加载");
        // 尝试预加载缓存
        List<PathPattern> patterns = buildAndCompilePatterns();
        cachedPatterns.set(patterns);
        if (!patterns.isEmpty()) {
            List<String> rawPatterns = buildRawPatterns();
            log.info("白名单配置加载成功，共 {} 个模式:\n  {}",
                    patterns.size(), String.join("\n  ", rawPatterns));
        } else {
            log.warn("白名单配置为空，未配置任何白名单路径，可能导致部分接口无法访问");
        }
    }

    /**
     * 监听 Nacos 配置变更事件，当白名单配置更新时刷新缓存
     */
    @EventListener
    public void handleEnvironmentChange(EnvironmentChangeEvent event) {
        Set<String> keys = event.getKeys();
        // 检查是否有白名单相关的配置变更
        boolean whitelistChanged = keys.stream()
                .anyMatch(key -> key.startsWith("whitelist."));

        if (whitelistChanged) {
            synchronized (this) {
                // 刷新缓存
                List<PathPattern> newPatterns = buildAndCompilePatterns();
                cachedPatterns.set(newPatterns);
                List<String> rawPatterns = buildRawPatterns();
                log.info("检测到白名单配置变更，已刷新缓存，新缓存共 {} 个模式:\n  {}",
                        newPatterns.size(), String.join("\n  ", rawPatterns));
            }
        }
    }

    /**
     * 规范化路径，确保以 / 开头
     * <p>
     * 统一处理路径规范化逻辑：
     * - 如果路径为 null 或空白，返回空字符串
     * - 如果路径已经以 / 开头，直接返回
     * - 否则添加 / 前缀
     * </p>
     *
     * @param path 待规范化的路径
     * @return 规范化后的路径
     */
    protected String normalizePath(String path) {
        if (path == null || path.isBlank()) {
            return "";
        }
        // 如果路径已经以 / 开头，直接返回；否则添加 / 前缀
        return path.startsWith("/") ? path : "/" + path;
    }

    /**
     * 获取白名单模式字符串数组（用于 Spring Security 配置）
     * <p>
     * Spring Security 的 requestMatchers() 需要字符串数组或 RequestMatcher
     * </p>
     *
     * @return 白名单模式字符串数组
     */
    public String[] getWhitelistPatternStrings() {
        List<String> rawPatterns = buildRawPatterns();
        return rawPatterns.stream()
                .map(this::normalizePath)
                .filter(pattern -> !pattern.isEmpty())
                .toArray(String[]::new);
    }
}

