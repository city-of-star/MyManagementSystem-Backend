package com.mms.gateway.utils;

import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.List;

/**
 * 实现功能【网关路径匹配工具类】
 * <p>
 * 统一处理路径匹配逻辑，支持白名单匹配
 * 基于Spring Web的路径模式匹配，支持通配符和路径变量
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-01 15:38:28
 */
public class GatewayPathMatcherUtils {

    /**
     * 路径模式解析器（线程安全）
     */
    private static final PathPatternParser PATH_PATTERN_PARSER = new PathPatternParser();

    /**
     * 检查路径是否匹配白名单中的任意一个模式
     * 1. 精确匹配: "/api/users" - 只匹配完全相同的路径
     * 2. 单层通配符: "/api/*" - 匹配/api下的所有单层路径
     * 3. 多层通配符: "/api/**" - 匹配/api下的所有路径（包括多级子路径）
     * 4. 路径变量: "/api/users/{id}" - 匹配路径中的动态部分
     * 5. 正则表达式: "/api/users/{id:\\d+}" - 使用正则约束路径变量
     * 6. 扩展名匹配: "*.html" - 匹配所有html文件
     *
     * @param path      待检查的路径
     * @param whitelist 白名单路径模式列表
     * @return 如果匹配则返回 true，否则返回 false
     */
    public static boolean isWhitelisted(String path, List<String> whitelist) {
        if (path == null || whitelist == null || whitelist.isEmpty()) {
            return false;
        }

        PathContainer pathContainer = PathContainer.parsePath(path);
        for (String pattern : whitelist) {
            PathPattern compiledPattern = PATH_PATTERN_PARSER.parse(pattern);
            if (compiledPattern.matches(pathContainer)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 私有构造函数，防止实例化
     */
    private GatewayPathMatcherUtils() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }
}