package com.mms.job.common.annotation;

import com.mms.job.common.enums.JobTypeEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 实现功能【定时任务处理器定义注解】
 * <p>
 * 仅包含一个必须的属性：任务类型枚举值 JobTypeEnum
 * 使用示例：@JobDefinition(JobTypeEnum.ATTACHMENT_CLEAN)
 * <p>
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface JobDefinition {

    /**
     * 任务类型枚举
     */
    JobTypeEnum value();
}
