package com.mms.common.core.utils;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

/**
 * 实现功能【全局ID生成工具类】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-09 17:53:36
 */
public final class IdUtils {

    private static final char[] NUMERIC = "0123456789".toCharArray();
    private static final char[] ALPHANUMERIC = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private static final Random RANDOM = new SecureRandom();

    /**
     * 生成标准 UUID（36位，包含横线）
     */
    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成去掉横线的 UUID（32位）
     */
    public static String uuid32() {
        return uuid().replace("-", "");
    }

    /**
     * 生成指定长度的 UUID 前缀（基于去横线的32位UUID截取）
     *
     * @param length 期望长度，<= 32
     */
    public static String uuid(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("length must be positive");
        }
        String id = uuid32();
        return id.length() <= length ? id : id.substring(0, length);
    }

    /**
     * 生成指定长度的纯数字随机串
     */
    public static String randomNumeric(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("length must be positive");
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(NUMERIC[RANDOM.nextInt(NUMERIC.length)]);
        }
        return sb.toString();
    }

    /**
     * 生成指定长度的字母数字混合随机串
     */
    public static String randomAlphanumeric(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("length must be positive");
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC[RANDOM.nextInt(ALPHANUMERIC.length)]);
        }
        return sb.toString();
    }

    /**
     * 生成通用ID：时间戳 + 8位UUID前缀
     */
    public static String timestampId() {
        return DateUtils.nowTimestamp() + "_" + uuid(8);
    }

    private IdUtils() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }
}
