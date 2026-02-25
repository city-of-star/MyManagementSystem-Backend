package com.mms.base.service.job;

import lombok.Getter;

/**
 * 实现功能【定时任务编码枚举】
 * <p>
 * 用于规范化管理各类定时任务的编码，避免前后端随意填写字符串。
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-25 11:08:53
 */
@Getter
public enum JobCodeEnum {

    /**
     * 附件物理清理任务
     */
    ATTACHMENT_CLEAN("ATTACHMENT_CLEAN", "附件物理清理任务");

    /**
     * 任务编码
     */
    private final String code;

    /**
     * 中文描述，便于展示
     */
    private final String description;

    JobCodeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }
}