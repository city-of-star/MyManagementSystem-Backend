package com.mms.common.core.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 实现功能【日期工具类】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-09 17:10:41
 */
public final class DateUtils {

    /**
     * 默认时区
     */
    public static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();

    /**
     * 常用格式：完整日期时间
     */
    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";

    /**
     * 常用格式：仅日期
     */
    public static final String PATTERN_DATE = "yyyy-MM-dd";

    /**
     * 常用格式：仅时间
     */
    public static final String PATTERN_TIME = "HH:mm:ss";

    /**
     * 用于按日期分目录的路径格式，例如：2026/02/09
     */
    public static final String PATTERN_DATE_DIR = "yyyy/MM/dd";

    /**
     * 用于生成不重复的文件名（如订单号、交易号、支付单号等）
     */
    public static final String PATTERN_TIMESTAMP = "yyyyMMddHHmmss";

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(PATTERN_DATETIME);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(PATTERN_DATE);

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(PATTERN_TIME);

    /**
     * 获取当前时间（使用默认时区）
     */
    public static LocalDateTime now() {
        return LocalDateTime.now(DEFAULT_ZONE);
    }

    /**
     * 获取今天的日期（使用默认时区）
     */
    public static LocalDate today() {
        return LocalDate.now(DEFAULT_ZONE);
    }

    /**
     * 将 LocalDateTime 按默认格式 yyyy-MM-dd HH:mm:ss 格式化
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DATETIME_FORMATTER);
    }

    /**
     * 将 LocalDate 按默认格式 yyyy-MM-dd 格式化
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DATE_FORMATTER);
    }

    /**
     * 将字符串按默认格式 yyyy-MM-dd HH:mm:ss 解析为 LocalDateTime
     */
    public static LocalDateTime parseDateTime(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(text, DATETIME_FORMATTER);
    }

    /**
     * 将字符串按默认格式 yyyy-MM-dd 解析为 LocalDate
     */
    public static LocalDate parseDate(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        return LocalDate.parse(text, DATE_FORMATTER);
    }

    /**
     * 生成按日期分目录的字符串，例如：2026/02/09
     */
    public static String todayDir() {
        return now().format(DateTimeFormatter.ofPattern(PATTERN_DATE_DIR));
    }

    /**
     * 生成不重复的文件名（如订单号、交易号、支付单号等）
     */
    public static String nowTimestamp() {
        return now().format(DateTimeFormatter.ofPattern(PATTERN_TIMESTAMP));
    }

    private DateUtils() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }
}
