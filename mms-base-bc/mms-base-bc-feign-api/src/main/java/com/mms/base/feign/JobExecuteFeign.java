package com.mms.base.feign;

import com.mms.common.core.response.Response;
import com.mms.common.job.dto.JobExecuteDto;
import com.mms.common.job.web.JobExecuteApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 实现功能【定时任务执行 Feign 客户端】
 * <p>
 * 定时任务调度中心通过此 Feign 接口远程调用此服务内部的定时任务处理器
 * </p>
 *
 * @author li.hongyu
 * @date 2026-02-27 14:06:21
 */
@FeignClient(name = "base")
public interface JobExecuteFeign extends JobExecuteApi {

    @Override
    @PostMapping(JobExecuteApi.JOB_EXECUTE_PATH)
    Response<?> execute(@RequestBody JobExecuteDto dto);
}