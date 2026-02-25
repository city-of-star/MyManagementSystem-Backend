package com.mms.base.service.job;

import lombok.Getter;

/**
 * 实现功能【定时任务编码枚举】
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
     * 中文描述，便于展示
     */
    private final String description;

    JobTypeEnum(String type, String description) {
        this.type = type;
        this.description = description;
    }
}