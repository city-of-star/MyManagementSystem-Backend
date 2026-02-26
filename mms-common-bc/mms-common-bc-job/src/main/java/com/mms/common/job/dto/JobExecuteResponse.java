package com.mms.common.job.dto;

import lombok.Data;

/**
 * 实现功能【通用任务执行响应】
 * <p>
 * 业务服务执行完成后返回调度中心，用于记录执行结果。
 *
 * @author li.hongyu
 * @date 2026-02-26 17:57:37
 */
@Data
public class JobExecuteResponse {

    /**
     * 是否执行成功
     */
    private boolean success;

    /**
     * 错误信息（success=false 时可填）
     */
    private String message;

    public static JobExecuteResponse ok() {
        JobExecuteResponse r = new JobExecuteResponse();
        r.setSuccess(true);
        return r;
    }

    public static JobExecuteResponse fail(String message) {
        JobExecuteResponse r = new JobExecuteResponse();
        r.setSuccess(false);
        r.setMessage(message);
        return r;
    }
}

