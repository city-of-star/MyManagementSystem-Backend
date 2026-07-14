package com.mms.common.webmvc.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mms.common.core.constants.gateway.GatewayConstants;
import com.mms.common.core.context.UserContext;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.utils.DateUtils;
import com.mms.common.core.utils.IdUtils;
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

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * 实现功能【操作日志采集切面】
 * <p>
 * 拦截类或者接口上带 {@link RequiresPermission} 的注解，异步投递 MQ
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-29 14:00:00
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class OperationLogAspect {

    // 成功状态
    private static final int SUCCESS_STATUS = 1;
    // 失败状态
    private static final int FAIL_STATUS = 0;
    // 错误消息最大长度
    private static final int ERROR_MESSAGE_MAX_LENGTH = 512;

    // 操作日志发布者
    private final OperationLogPublisher operationLogPublisher;
    // 调度任务执行器
    private final ThreadPoolTaskExecutor schedulerTaskExecutor;
    // 对象映射器
    private final ObjectMapper objectMapper;

    /**
     * 拦截类或者接口上带 {@link RequiresPermission} 的注解，异步投递 MQ
     */
    @Around("@annotation(com.mms.common.security.servlet.annotations.RequiresPermission) || " +
            "@within(com.mms.common.security.servlet.annotations.RequiresPermission)")
    public Object aroundRequiresPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法签名 
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取方法
        Method method = signature.getMethod();
        // 获取方法上的 {@link RequiresPermission} 注解
        RequiresPermission requiresPermission = resolveRequiresPermission(method, joinPoint.getTarget().getClass());
        if (requiresPermission == null || !StringUtils.hasText(requiresPermission.value())) {
            // 如果注解不存在或者权限代码为空，则直接返回
            return joinPoint.proceed();
        }
        // 获取权限代码
        String permissionCode = requiresPermission.value();
        // 获取权限元数据
        Optional<OperationLogPermissionMeta> metaOptional = OperationLogPermissionMappings.resolve(permissionCode);
        // 如果权限元数据不存在，则直接返回
        if (metaOptional.isEmpty()) {
            return joinPoint.proceed();
        }
        // 获取 HTTP 方法   
        String httpMethod = OperationLogHttpMethodUtils.resolveHttpMethod(method);
        // 如果 HTTP 方法不是可记录的，则直接返回
        if (!OperationLogHttpMethodUtils.isRecordableHttpMethod(httpMethod)) {
            return joinPoint.proceed();
        }
        // 获取用户上下文
        UserContext userContext = UserContextUtils.getUserContext();
        // 如果用户上下文不存在，则直接返回
        if (!UserContextUtils.hasUserContext(userContext)) {
            return joinPoint.proceed();
        }
        // 获取当前请求
        HttpServletRequest request = UserContextUtils.getCurrentRequest();
        // 如果当前请求不存在，则直接返回
        if (request == null) {
            return joinPoint.proceed();
        }
        // 获取权限元数据
        OperationLogPermissionMeta meta = metaOptional.get();
        // 定义开始时间
        long startMs = System.currentTimeMillis();
        // 定义结果
        Object result = null;
        // 定义异常
        Throwable caught = null;
        try {
            // 执行方法
            result = joinPoint.proceed();
            return result;
        } catch (Throwable ex) {
            caught = ex;
            throw ex;
        } finally {
            if (shouldPublish(caught)) {
                // 异步投递 MQ
                publishAsync(buildPayload(
                        meta,
                        permissionCode,
                        userContext,
                        request,
                        joinPoint.getArgs(),
                        httpMethod,
                        result,
                        method.getReturnType() == void.class,
                        startMs,
                        caught
                ));
            }
        }
    }

    /**
     * 判断是否需要投递 MQ
     */
    private boolean shouldPublish(Throwable caught) {
        // 如果异常为空，则需要投递 MQ
        if (caught == null) {
            return true;
        }
        // 如果异常是业务异常，且业务异常的错误码不是无权限错误码，则需要投递 MQ
        if (caught instanceof BusinessException businessException) {
            return businessException.getCode() != ErrorCode.NO_PERMISSION.getCode();
        }
        // 其他情况都需要投递 MQ
        return true;
    }

    /**
     * 构建 MQ 消息
     */
    private OperationLogRecordMqPayload buildPayload(OperationLogPermissionMeta meta,
                                                   String permissionCode,
                                                   UserContext userContext,
                                                   HttpServletRequest request,
                                                   Object[] methodArgs,
                                                   String httpMethod,
                                                   Object result,
                                                   boolean voidReturn,
                                                   long startMs,
                                                   Throwable caught) {
        // 创建 MQ 消息
        OperationLogRecordMqPayload payload = new OperationLogRecordMqPayload();
        // 设置 ID
        payload.setId(IdUtils.nextId());
        // 设置 traceId
        payload.setTraceId(MDC.get(GatewayConstants.Mdc.TRACE_ID));
        // 设置用户 ID
        payload.setUserId(userContext.getUserId());
        // 设置用户名
        payload.setUsername(userContext.getUsername());
        // 设置模块
        payload.setModule(meta.module());
        // 设置操作类型
        payload.setOperationType(meta.operationType());
        // 设置操作描述
        payload.setOperationDesc(meta.operationDesc());
        // 设置请求方法
        payload.setRequestMethod(httpMethod);
        // 设置请求 URL
        payload.setRequestUrl(buildRequestUrl(request));
        // 设置请求 IP
        payload.setRequestIp(userContext.getClientIp());
        // 设置请求参数
        payload.setRequestParams(OperationLogPayloadUtils.buildRequestParams(request, methodArgs, objectMapper));
        // 设置响应数据
        payload.setResponseData(OperationLogPayloadUtils.buildResponseSummary(result, voidReturn, objectMapper));
        // 设置耗时
        payload.setCostMs(System.currentTimeMillis() - startMs);
        // 设置操作时间
        payload.setOperationTime(DateUtils.now());
        // 如果异常为空，则设置操作状态为成功
        if (caught == null) {
            payload.setOperationStatus(SUCCESS_STATUS);
        } else {
            // 如果异常不为空，则设置操作状态为失败
            payload.setOperationStatus(FAIL_STATUS);
            // 设置错误消息
            payload.setErrorMessage(truncateErrorMessage(caught.getMessage()));
        }
        // 如果 traceId 为空，则记录日志
        if (!StringUtils.hasText(payload.getTraceId())) {
            log.debug("操作日志缺少 traceId, permissionCode={}", permissionCode);
        }
        return payload;
    }

    /**
     * 异步投递 MQ
     */
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

    /**
     * 解析 {@link RequiresPermission} 注解
     */
    private RequiresPermission resolveRequiresPermission(Method method, Class<?> targetClass) {
        RequiresPermission methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, RequiresPermission.class);
        if (methodAnnotation != null) {
            return methodAnnotation;
        }
        return AnnotatedElementUtils.findMergedAnnotation(targetClass, RequiresPermission.class);
    }

    /**
     * 构建请求 URL
     */
    private String buildRequestUrl(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        if (!StringUtils.hasText(queryString)) {
            return uri;
        }
        return uri + "?" + queryString;
    }

    /**
     * 截断错误消息
     */
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
