package com.mms.common.webmvc.audit;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mms.common.core.constants.gateway.GatewayConstants;
import com.mms.common.core.context.UserContext;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.utils.DateUtils;
import com.mms.common.security.servlet.annotations.RequiresPermission;
import com.mms.common.webmvc.utils.UserContextUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * 实现功能【操作日志采集切面】
 * <p>
 * 拦截带 {@link RequiresPermission} 的写操作接口，异步投递 MQ。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-29 14:00:00
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class OperationLogAspect {

    private static final int SUCCESS_STATUS = 1;
    private static final int FAIL_STATUS = 0;
    private static final int ERROR_MESSAGE_MAX_LENGTH = 512;

    private final OperationLogPublisher operationLogPublisher;
    private final ThreadPoolTaskExecutor schedulerTaskExecutor;
    private final ObjectMapper objectMapper;

    @Around("@annotation(com.mms.common.security.servlet.annotations.RequiresPermission) || " +
            "@within(com.mms.common.security.servlet.annotations.RequiresPermission)")
    public Object aroundRequiresPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequiresPermission requiresPermission = resolveRequiresPermission(method, joinPoint.getTarget().getClass());
        if (requiresPermission == null || !StringUtils.hasText(requiresPermission.value())) {
            return joinPoint.proceed();
        }

        String permissionCode = requiresPermission.value();
        Optional<OperationLogPermissionMeta> metaOptional = OperationLogPermissionMappings.resolve(permissionCode);
        if (metaOptional.isEmpty()) {
            return joinPoint.proceed();
        }

        String httpMethod = OperationLogHttpMethodUtils.resolveHttpMethod(method);
        if (!OperationLogHttpMethodUtils.isRecordableHttpMethod(httpMethod)) {
            return joinPoint.proceed();
        }

        UserContext userContext = UserContextUtils.getUserContext();
        if (!hasUserContext(userContext)) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return joinPoint.proceed();
        }

        OperationLogPermissionMeta meta = metaOptional.get();
        long startMs = System.currentTimeMillis();
        Object result = null;
        Throwable caught = null;
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable ex) {
            caught = ex;
            throw ex;
        } finally {
            if (shouldPublish(caught)) {
                publishAsync(buildPayload(
                        meta,
                        permissionCode,
                        userContext,
                        request,
                        httpMethod,
                        result,
                        method.getReturnType() == void.class,
                        startMs,
                        caught
                ));
            }
        }
    }

    private boolean shouldPublish(Throwable caught) {
        if (caught == null) {
            return true;
        }
        if (caught instanceof BusinessException businessException) {
            return businessException.getCode() != ErrorCode.NO_PERMISSION.getCode();
        }
        return true;
    }

    private OperationLogRecordMqPayload buildPayload(OperationLogPermissionMeta meta,
                                                   String permissionCode,
                                                   UserContext userContext,
                                                   HttpServletRequest request,
                                                   String httpMethod,
                                                   Object result,
                                                   boolean voidReturn,
                                                   long startMs,
                                                   Throwable caught) {
        OperationLogRecordMqPayload payload = new OperationLogRecordMqPayload();
        payload.setId(IdWorker.getId());
        payload.setTraceId(MDC.get(GatewayConstants.Mdc.TRACE_ID));
        payload.setUserId(userContext.getUserId());
        payload.setUsername(userContext.getUsername());
        payload.setModule(meta.module());
        payload.setOperationType(meta.operationType());
        payload.setOperationDesc(meta.operationDesc());
        payload.setRequestMethod(httpMethod);
        payload.setRequestUrl(buildRequestUrl(request));
        payload.setRequestIp(userContext.getClientIp());
        payload.setRequestParams(OperationLogPayloadUtils.buildRequestParams(request, objectMapper));
        payload.setResponseData(OperationLogPayloadUtils.buildResponseSummary(result, voidReturn, objectMapper));
        payload.setCostMs(System.currentTimeMillis() - startMs);
        payload.setOperationTime(DateUtils.now());
        if (caught == null) {
            payload.setOperationStatus(SUCCESS_STATUS);
        } else {
            payload.setOperationStatus(FAIL_STATUS);
            payload.setErrorMessage(truncateErrorMessage(caught.getMessage()));
        }
        if (!StringUtils.hasText(payload.getTraceId())) {
            log.debug("操作日志缺少 traceId, permissionCode={}", permissionCode);
        }
        return payload;
    }

    private void publishAsync(OperationLogRecordMqPayload payload) {
        String traceId = payload.getTraceId();
        schedulerTaskExecutor.execute(() -> {
            try {
                if (StringUtils.hasText(traceId)) {
                    MDC.put(GatewayConstants.Mdc.TRACE_ID, traceId);
                }
                operationLogPublisher.publish(payload);
            } finally {
                MDC.remove(GatewayConstants.Mdc.TRACE_ID);
            }
        });
    }

    private RequiresPermission resolveRequiresPermission(Method method, Class<?> targetClass) {
        RequiresPermission methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, RequiresPermission.class);
        if (methodAnnotation != null) {
            return methodAnnotation;
        }
        return AnnotatedElementUtils.findMergedAnnotation(targetClass, RequiresPermission.class);
    }

    private boolean hasUserContext(UserContext userContext) {
        if (userContext == null) {
            return false;
        }
        return userContext.getUserId() != null || StringUtils.hasText(userContext.getUsername());
    }

    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        return attributes.getRequest();
    }

    private String buildRequestUrl(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        if (!StringUtils.hasText(queryString)) {
            return uri;
        }
        return uri + "?" + queryString;
    }

    private String truncateErrorMessage(String message) {
        if (!StringUtils.hasText(message)) {
            return null;
        }
        if (message.length() <= ERROR_MESSAGE_MAX_LENGTH) {
            return message;
        }
        return message.substring(0, ERROR_MESSAGE_MAX_LENGTH);
    }
}
