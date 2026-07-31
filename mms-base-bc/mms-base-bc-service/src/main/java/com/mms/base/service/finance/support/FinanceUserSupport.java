package com.mms.base.service.finance.support;

import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.webmvc.utils.UserContextUtils;

/**
 * 实现功能【记账用户上下文工具】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
public final class FinanceUserSupport {

    /**
     * 获取当前登录用户ID，未登录时抛出业务异常
     */
    public static Long requireUserId() {
        Long userId = UserContextUtils.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.LOGIN_EXPIRED, "登录信息已过期，请重新登录");
        }
        return userId;
    }

    /**
     * 校验实体归属当前用户，不匹配时视为不存在
     */
    public static void requireOwned(Long entityUserId, String notFoundMessage) {
        Long userId = requireUserId();
        if (entityUserId == null || !entityUserId.equals(userId)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, notFoundMessage);
        }
    }

    private FinanceUserSupport() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }
}
