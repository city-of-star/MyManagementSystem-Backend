package com.mms.common.job;

import com.mms.job.common.annotation.JobDefinition;
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
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-25 11:07:06
 */
@Slf4j
@Component
public class JobHandlerRegistry {

    /**
     * 任务类型 -> 处理器实例
     */
    private final Map<String, JobHandler> handlerMap = new HashMap<>();

    /**
     * 通过构造函数注入当前容器中所有的 JobHandler，
     * 并根据其类上的 @JobDefinition 注解完成注册。
     *
     * @param handlers 所有 JobHandler 实现
     */
    public JobHandlerRegistry(List<JobHandler> handlers) {
        if (handlers == null || handlers.isEmpty()) {
            log.warn("未发现任何 JobHandler 实现，定时任务处理器注册中心为空");
            return;
        }
        for (JobHandler handler : handlers) {
            Class<?> targetClass = AopUtils.getTargetClass(handler);
            JobDefinition definition = AnnotationUtils.findAnnotation(targetClass, JobDefinition.class);
            if (definition == null) {
                // 未标注 JobDefinition 的 Handler 不参与注册
                log.debug("JobHandler 未标注 @JobDefinition，跳过注册：{}", handler.getClass().getName());
                continue;
            }
            // 从注解中获取任务类型枚举，并转换为字符串标识
            String jobType = definition.value().getType();
            if (!StringUtils.hasText(jobType)) {
                log.warn("@JobDefinition 未提供有效的 JobTypeEnum，跳过注册，handler={}", handler.getClass().getName());
                continue;
            }
            if (handlerMap.containsKey(jobType)) {
                log.warn("检测到重复的 jobType={}，后注册的 handler={} 将覆盖之前的处理器={}",
                        jobType, handler.getClass().getName(), handlerMap.get(jobType).getClass().getName());
            }
            handlerMap.put(jobType, handler);
            log.info("注册定时任务处理器：jobType={}，handler={}", jobType, handler.getClass().getSimpleName());
        }
    }

    /**
     * 根据任务类型获取对应处理器
     *
     * @param jobType 任务类型
     * @return 处理器实例，找不到时返回 null
     */
    public JobHandler getHandler(String jobType) {
        if (!StringUtils.hasText(jobType)) {
            return null;
        }
        return handlerMap.get(jobType);
    }
}
