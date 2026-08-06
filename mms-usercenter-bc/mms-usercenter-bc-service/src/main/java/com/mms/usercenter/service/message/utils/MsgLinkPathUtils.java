package com.mms.usercenter.service.message.utils;

import com.mms.common.core.exceptions.BusinessException;
import org.springframework.util.StringUtils;

/**
 * 实现功能【站内跳转路径校验】
 * <p>
 * 仅允许相对路径，禁止外链与脚本协议。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-06
 */
public final class MsgLinkPathUtils {

    private static final int MAX_LEN = 200;

    private MsgLinkPathUtils() {
    }

    /**
     * 规范化可选跳转路径：空白 → null；有值则 trim 并校验。
     */
    public static String normalizeOptional(String raw) {
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        String path = raw.trim();
        if (path.length() > MAX_LEN) {
            throw new BusinessException("跳转路径长度不能超过" + MAX_LEN + "个字符");
        }
        if (!path.startsWith("/") || path.startsWith("//")) {
            throw new BusinessException("跳转路径须为以 / 开头的站内相对路径");
        }
        String lower = path.toLowerCase();
        if (lower.contains("://")
                || lower.contains("javascript:")
                || lower.contains("data:")
                || path.indexOf('\\') >= 0
                || path.indexOf(' ') >= 0
                || path.indexOf('<') >= 0
                || path.indexOf('>') >= 0
                || path.indexOf('"') >= 0
                || path.indexOf('\'') >= 0) {
            throw new BusinessException("跳转路径含非法字符");
        }
        return path;
    }
}
