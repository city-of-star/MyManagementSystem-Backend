package com.mms.common.job.web;

import com.mms.common.core.response.Response;
import com.mms.common.job.dto.JobExecuteDto;

/**
 * 实现功能【通用定时任务执行 API 契约】
 * <p>
 * 所有需要被定时任务调度平台远程调用定时任务的服务，都应新建 JobExecuteFeign 接口并继承此接口
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-27 14:07:57
 */
public interface JobExecuteApi {

    /**
     * 通用任务执行接口路径
     */
    String JOB_EXECUTE_PATH = "/internal/job/execute";

    /**
     * 执行任务
     */
    Response<?> execute(JobExecuteDto dto);
}