package com.mms.common.webmvc.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mms.common.core.response.Response;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 实现功能【操作日志请求/响应摘要工具】
 * <p>
 * TODO: request_params 脱敏规则（password/token 等敏感字段）。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-29 14:00:00
 */
public final class OperationLogPayloadUtils {

    private OperationLogPayloadUtils() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }

    /**
     * 构建请求参数摘要（query + body + 控制器入参）
     */
    public static String buildRequestParams(HttpServletRequest request, Object[] methodArgs, ObjectMapper objectMapper) {
        Map<String, Object> root = new LinkedHashMap<>();
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap != null && !parameterMap.isEmpty()) {
            Map<String, Object> query = new LinkedHashMap<>();
            parameterMap.forEach((key, values) -> {
                if (values == null) {
                    query.put(key, null);
                } else if (values.length == 1) {
                    query.put(key, values[0]);
                } else {
                    query.put(key, values);
                }
            });
            root.put("query", query);
        }
        ContentCachingRequestWrapper cachingRequest = resolveContentCachingRequest(request);
        if (cachingRequest != null) {
            byte[] content = cachingRequest.getContentAsByteArray();
            if (content.length > 0) {
                String body = new String(content, StandardCharsets.UTF_8);
                root.put("body", parseBody(body, objectMapper));
            }
        }
        if (!root.containsKey("body")) {
            List<Object> loggableArgs = collectLoggableMethodArgs(methodArgs);
            if (!loggableArgs.isEmpty()) {
                root.put("args", loggableArgs.size() == 1 ? loggableArgs.get(0) : loggableArgs);
            }
        }
        if (root.isEmpty()) {
            return null;
        }
        try {
            return truncate(objectMapper.writeValueAsString(root), OperationLogConstants.REQUEST_PARAMS_MAX_LENGTH);
        } catch (JsonProcessingException ex) {
            return truncate(String.valueOf(root), OperationLogConstants.REQUEST_PARAMS_MAX_LENGTH);
        }
    }

    /**
     * 构建响应摘要
     */
    public static String buildResponseSummary(Object result, boolean fileExport, ObjectMapper objectMapper) {
        Map<String, Object> summary = new LinkedHashMap<>();
        if (fileExport) {
            summary.put("code", Response.SUCCESS_CODE);
            summary.put("message", Response.SUCCESS_MESSAGE);
            summary.put("type", "file_export");
        } else if (result instanceof Response<?> response) {
            summary.put("code", response.getCode());
            summary.put("message", response.getMessage());
        } else if (result != null) {
            summary.put("type", result.getClass().getSimpleName());
        } else {
            summary.put("code", Response.SUCCESS_CODE);
            summary.put("message", Response.SUCCESS_MESSAGE);
        }
        try {
            return objectMapper.writeValueAsString(summary);
        } catch (JsonProcessingException ex) {
            return summary.toString();
        }
    }

    /**
     * 沿 {@link HttpServletRequestWrapper} 链查找 {@link ContentCachingRequestWrapper}
     * <p>
     * {@link RequestContextHolder} 中的 request 常被 Spring Security 等外层包装，
     * 直接 {@code instanceof} 会失败导致 body 为空。
     * </p>
     */
    static ContentCachingRequestWrapper resolveContentCachingRequest(HttpServletRequest request) {
        HttpServletRequest current = request;
        while (current != null) {
            if (current instanceof ContentCachingRequestWrapper cachingRequest) {
                return cachingRequest;
            }
            if (current instanceof HttpServletRequestWrapper wrapper) {
                ServletRequest wrapped = wrapper.getRequest();
                if (wrapped instanceof HttpServletRequest httpRequest) {
                    current = httpRequest;
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        return null;
    }

    private static List<Object> collectLoggableMethodArgs(Object[] methodArgs) {
        if (methodArgs == null || methodArgs.length == 0) {
            return List.of();
        }
        List<Object> loggableArgs = new ArrayList<>();
        for (Object arg : methodArgs) {
            if (arg == null || !isLoggableMethodArg(arg)) {
                continue;
            }
            loggableArgs.add(arg);
        }
        return loggableArgs;
    }

    private static boolean isLoggableMethodArg(Object arg) {
        return !(arg instanceof ServletRequest)
                && !(arg instanceof ServletResponse)
                && !(arg instanceof BindingResult)
                && !(arg instanceof MultipartFile);
    }

    private static Object parseBody(String body, ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(body, Object.class);
        } catch (Exception ex) {
            return body;
        }
    }

    private static String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
