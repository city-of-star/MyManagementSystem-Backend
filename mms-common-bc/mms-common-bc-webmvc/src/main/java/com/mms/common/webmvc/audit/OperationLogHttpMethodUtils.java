package com.mms.common.webmvc.audit;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * 实现功能【操作日志 HTTP 方法解析工具】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-05-29 14:00:00
 */
public final class OperationLogHttpMethodUtils {

    private static final Set<String> RECORDABLE_METHODS = Set.of("POST", "PUT", "DELETE");

    private OperationLogHttpMethodUtils() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }

    /**
     * 解析 Controller 方法对应的 HTTP 方法
     */
    public static String resolveHttpMethod(Method method) {
        if (method == null) {
            return null;
        }
        if (method.isAnnotationPresent(PostMapping.class)) {
            return "POST";
        }
        if (method.isAnnotationPresent(PutMapping.class)) {
            return "PUT";
        }
        if (method.isAnnotationPresent(DeleteMapping.class)) {
            return "DELETE";
        }
        if (method.isAnnotationPresent(GetMapping.class)) {
            return "GET";
        }
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        if (requestMapping != null && requestMapping.method().length == 1) {
            return requestMapping.method()[0].name();
        }
        return null;
    }

    /**
     * 是否为需要记录的写操作 HTTP 方法
     */
    public static boolean isRecordableHttpMethod(String httpMethod) {
        return httpMethod != null && RECORDABLE_METHODS.contains(httpMethod);
    }
}
