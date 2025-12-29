package com.mms.common.core.aop;

import com.mms.common.core.annotations.RequiresPermission;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * 实现功能【权限校验切面】
 * <p>
 * 拦截标注了 {@link RequiresPermission} 的方法/类，
 * 从 Spring Security 的 SecurityContext 中获取当前用户权限，
 * 验证是否包含指定的权限编码。
 * 如无权限，则抛出 {@link BusinessException}，错误码为 {@link ErrorCode#NO_PERMISSION}。
 * </p>
 *
 * 说明：
 * - 该切面依赖 Spring Security 已经在当前线程设置了 Authentication（JwtAuthenticationFilter 已完成）
 * - 校验逻辑基于 authorities 中的权限字符串，与 UserDetailsServiceImpl / SecurityUser 保持一致
 *
 * @author li.hongyu
 * @date 2025-12-19 10:35:26
 */
@Slf4j
@Aspect
@Component
public class PermissionCheckAspect {

    /**
     * 在执行标注了 @RequiresPermission 的方法前进行权限校验
     */
    @Before("@within(requiresPermission) || @annotation(requiresPermission)")
    public void checkPermission(RequiresPermission requiresPermission) {
        if (requiresPermission == null) {
            // 类上标注时，方法上未标注，AOP 可能传入 null，这里直接返回交给其他切面处理
            return;
        }

        String requiredPermission = requiresPermission.value();
        if (!StringUtils.hasText(requiredPermission)) {
            // 未指定权限编码，直接放行
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            log.warn("权限校验失败：未找到认证信息，requiredPermission={}", requiredPermission);
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities == null || authorities.isEmpty()) {
            log.warn("权限校验失败：当前用户未分配任何权限，requiredPermission={}", requiredPermission);
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }

        boolean hasPermission = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(requiredPermission::equals);

        if (!hasPermission) {
            log.warn("权限校验失败：缺少权限，requiredPermission={}，currentAuthorities={}",
                    requiredPermission, authorities);
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }
    }
}


