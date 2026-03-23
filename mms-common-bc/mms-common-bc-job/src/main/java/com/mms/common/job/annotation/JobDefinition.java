package com.mms.common.job.annotation;

import com.mms.common.job.enums.JobTypeEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 实现功能【定时任务处理器定义注解】
 * <p>
 * 包含任务类型枚举和参数DTO类型
 * 使用示例：@JobDefinition(type = JobTypeEnum.ATTACHMENT_CLEAN, paramClass = AttachmentCleanJobDto.class)
 * <p>
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface JobDefinition {

    /**
     * 任务类型枚举
     */
    JobTypeEnum type();

    /**
     * 任务参数DTO类型
     */
    Class<?> paramClass();
}
