package com.mms.job.common.enums;

import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 * 实现功能【定时任务执行记录状态枚举】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-22 14:00:00
 */
@Getter
public enum JobRunStatusEnum {

    RUNNING("running", "运行中"),
    SUCCESS("success", "成功"),
    FAIL("fail", "失败"),
    TIMEOUT("timeout", "超时"),
    SKIP("skip", "跳过");

    private static final Set<JobRunStatusEnum> RETRYABLE = EnumSet.of(FAIL, TIMEOUT, SKIP);

    private final String code;

    private final String label;

    JobRunStatusEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }

    /**
     * 是否与给定状态码相同（忽略大小写）
     */
    public boolean matches(String status) {
        return status != null && code.equalsIgnoreCase(status.trim());
    }

    /**
     * 是否允许重试（失败、超时、已跳过/终止）
     */
    public static boolean isRetryable(String status) {
        return RETRYABLE.stream().anyMatch(item -> item.matches(status));
    }

    /**
     * 按状态码解析枚举
     */
    public static JobRunStatusEnum fromCode(String status) {
        if (!StringUtils.hasText(status)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(item -> item.matches(status))
                .findFirst()
                .orElse(null);
    }
}
