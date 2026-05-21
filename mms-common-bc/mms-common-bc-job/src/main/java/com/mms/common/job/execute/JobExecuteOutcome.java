package com.mms.common.job.execute;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 实现功能【定时任务本地执行结果】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-20 10:00:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobExecuteOutcome {

    private boolean success;

    private String resultJson;

    private String errorMessage;

    private String errorStack;

    public static JobExecuteOutcome success(String resultJson) {
        return new JobExecuteOutcome(true, resultJson, null, null);
    }

    public static JobExecuteOutcome fail(String errorMessage, String errorStack) {
        return new JobExecuteOutcome(false, null, errorMessage, errorStack);
    }
}
