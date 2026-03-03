package com.mms.common.job.web;

import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.response.Response;
import com.mms.common.job.JobHandler;
import com.mms.common.job.JobHandlerRegistry;
import com.mms.common.job.dto.JobExecuteDto;
import com.mms.common.job.dto.JobValidateDto;
import com.mms.common.job.utils.JobParamUtils;
import com.mms.job.common.enums.JobTypeEnum;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 实现功能【定时任务执行入口】
 * <p>
 * 定时任务调度平台通过调用此服务来远程调用当前服务的定时任务
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-26 17:57:37
 */
@Tag(name = "定时任务执行入口", description = "定时任务执行入口")
@Slf4j
@RestController
@RequestMapping("/internal/job")
@AllArgsConstructor
public class JobExecuteController {

    /**
     * 任务处理器注册中心
     */
    private final JobHandlerRegistry jobHandlerRegistry;

    /**
     * 执行任务
     */
    @PostMapping("/execute")
    public Response<?> execute(@RequestBody JobExecuteDto dto) {
        if (dto == null || !StringUtils.hasText(dto.getJobType())) {
            return Response.error(ErrorCode.PARAM_INVALID.getCode(), "任务类型（jobType）不能为空");
        }
        // 判断是否有此类型的任务处理器
        String jobType = dto.getJobType();
        String jobName = JobTypeEnum.getNameByType(jobType);
        JobHandler handler = jobHandlerRegistry.getHandler(jobType);
        if (handler == null) {
            return Response.error(ErrorCode.PARAM_INVALID.getCode(), "未找到任务处理器：" + jobName + "（" + jobType + "）");
        }
        // 执行任务
        long start = System.currentTimeMillis();
        try {
            log.info("开始执行任务，jobType={}，jobId={}，requestId={}", jobType, dto.getJobId(), dto.getRequestId());
            handler.execute(dto.getParamsJson());
            log.info("任务执行完成，jobType={}，耗时={}ms，requestId={}", jobType, System.currentTimeMillis() - start, dto.getRequestId());
            return Response.success();
        } catch (Exception e) {
            log.error("任务执行失败，jobType={}，耗时={}ms，requestId={}，错误：{}", jobType, System.currentTimeMillis() - start, dto.getRequestId(), e.getMessage(), e);
            return Response.error(ErrorCode.SYSTEM_ERROR.getCode(), e.getMessage());
        }
    }

    /**
     * 验证JSON参数是否能被正确解析
     */
    @PostMapping("/validate")
    public Response<?> validate(@RequestBody JobValidateDto dto) {
        if (dto == null || !StringUtils.hasText(dto.getJobType())) {
            return Response.error(ErrorCode.PARAM_INVALID.getCode(), "任务类型（jobType）不能为空");
        }
        // 根据任务处理器类型获取对应的Dto类型
        Class<?> paramClass = jobHandlerRegistry.getParamClass(dto.getJobType());
        // 解析JSON参数
        try {
            JobParamUtils.parseParams(dto.getParamsJson(), paramClass);
        } catch (Exception e) {
            return Response.error(ErrorCode.PARAM_INVALID.getCode(), e.getMessage());
        }
        return Response.success();
    }
}

