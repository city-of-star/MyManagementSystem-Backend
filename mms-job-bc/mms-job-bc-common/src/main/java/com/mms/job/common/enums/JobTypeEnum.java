package com.mms.job.common.enums;

import lombok.Getter;

/**
 * 实现功能【定时任务类型枚举】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-25 11:08:53
 */
@Getter
public enum JobTypeEnum {

    /**
     * 附件物理清理任务
     */
    ATTACHMENT_CLEAN("ATTACHMENT_CLEAN", "附件清理任务");

    /**
     * 任务类型
     */
    private final String type;

    /**
     * 任务名称
     */
    private final String name;

    JobTypeEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }

    /**
     * 根据任务类型获取任务名称
     */
    public static String getNameByType(String type) {
        if (type == null) {
            return null;
        }
        for (JobTypeEnum value : JobTypeEnum.values()) {
            if (value.type.equals(type)) {
                return value.name;
            }
        }
        return null;
    }
}