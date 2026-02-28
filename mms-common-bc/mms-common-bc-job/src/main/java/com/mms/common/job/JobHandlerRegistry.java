package com.mms.common.job;

import com.mms.job.common.annotation.JobDefinition;
import com.mms.job.common.enums.JobTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实现功能【定时任务处理器注册中心】
 * <p>
 * 每个服务都有自己的定时任务处理器注册中心
 * 注册自己的定时任务处理器
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-25 11:07:06
 */
@Slf4j
@Component
public class JobHandlerRegistry {

    /**
     * 任务处理器Map（任务类型 -> 处理器实例）
     */
    private final Map<String, JobHandler> handlerMap = new HashMap<>();

    /**
     * 任务参数DTO类型Map（任务类型 -> DTO类型）
     */
    private final Map<String, Class<?>> paramClassMap = new HashMap<>();

    /**
     * 注册当前服务的所有任务处理器
     */
    public JobHandlerRegistry(List<JobHandler> handlers) {
        long start = System.currentTimeMillis();
        log.info("定时任务处理器注册中心开始注册");
        if (handlers == null || handlers.isEmpty()) {
            log.warn("未发现任何处理器");
            return;
        }
        for (JobHandler handler : handlers) {
            Class<?> targetClass = AopUtils.getTargetClass(handler);
            JobDefinition definition = AnnotationUtils.findAnnotation(targetClass, JobDefinition.class);
            if (definition == null) {
                log.debug("{} 未标注 @JobDefinition，跳过注册", handler.getClass().getName());
                continue;
            }
            // 获取任务类型、参数DTO类型
            JobTypeEnum jobType = definition.type();
            Class<?> paramClass = definition.paramClass();
            if (jobType == null || paramClass == null) {
                log.warn("{} 注解中未提供任务处理器类型或者参数DTO类型，跳过注册", handler.getClass().getName());
                continue;
            }
            if (handlerMap.containsKey(jobType.getType())) {
                log.warn("检测到重复的任务处理器类型={}，后注册的处理器={} 将覆盖之前的处理器={}", jobType.getType(), handler.getClass().getName(), handlerMap.get(jobType.getType()).getClass().getName());
            }
            // 注册任务处理器和Dto类型
            handlerMap.put(jobType.getType(), handler);
            paramClassMap.put(jobType.getType(), paramClass);
            log.info("注册成功：任务处理器名称={}，任务处理器类型={}，任务处理器类名={}，参数DTO类型={}", jobType.getName(), jobType.getType(), handler.getClass().getSimpleName(), paramClass.getSimpleName());
        }
        long cost = System.currentTimeMillis() - start;
        log.info("定时任务处理器注册耗时={}ms", cost);
    }

    /**
     * 根据任务类型获取对应处理器
     */
    public JobHandler getHandler(String jobType) {
        if (!StringUtils.hasText(jobType)) {
            return null;
        }
        return handlerMap.get(jobType);
    }

    /**
     * 根据任务类型获取对应的参数DTO类型
     */
    public Class<?> getParamClass(String jobType) {
        if (!StringUtils.hasText(jobType)) {
            return null;
        }
        return paramClassMap.get(jobType);
    }
}
