package com.mms.intern.core.util;

import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.webmvc.utils.UserContextUtils;

public final class InternUserHelper {

    private InternUserHelper() {
    }

    public static Long requireUserId() {
        Long uid = UserContextUtils.getUserId();
        if (uid == null) {
            throw new BusinessException(ErrorCode.LOGIN_EXPIRED, "未登录或登录已失效");
        }
        return uid;
    }
}
