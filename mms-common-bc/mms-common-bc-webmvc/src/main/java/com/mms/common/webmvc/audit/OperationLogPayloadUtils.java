package com.mms.common.webmvc.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mms.common.core.response.Response;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
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
     * 构建请求参数摘要（query + body）
     */
    public static String buildRequestParams(HttpServletRequest request, ObjectMapper objectMapper) {
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
        if (request instanceof ContentCachingRequestWrapper cachingRequest) {
            byte[] content = cachingRequest.getContentAsByteArray();
            if (content.length > 0) {
                String body = new String(content, StandardCharsets.UTF_8);
                root.put("body", parseBody(body, objectMapper));
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
