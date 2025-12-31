package com.mms.common.security.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 实现功能【权限校验注解】
 * <p>
 * 用于在方法或类上声明所需的权限编码。
 * 执行时由 AOP 切面拦截并进行权限校验，
 * 如果当前用户不具备指定权限，则抛出 BusinessException。
 * </p>
 *
 * 使用示例：
 * <pre>
 * @RequiresPermission("user:view")
 * public Page<UserVo> getUserPage(...) { ... }
 * </pre>
 *
 * @author li.hongyu
 * @date 2025-12-19 14:46:39
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermission {

    /**
     * 所需的权限编码，例如："user:view"
     */
    String value();
}


