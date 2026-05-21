package com.mms.common.job.web;

import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.response.Response;
import com.mms.common.job.dto.JobValidateDto;
import com.mms.common.job.execute.JobExecuteExecutor;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 实现功能【定时任务参数校验 HTTP 入口】
 * <p>
 * 执行已改为 MQ；本接口仅供 job 调度中心在保存任务时远程校验参数 JSON。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-20 10:00:00
 */
@Tag(name = "定时任务参数校验", description = "定时任务参数校验")
@RestController
@RequestMapping("/internal/job")
@AllArgsConstructor
public class JobValidateController {

    private final JobExecuteExecutor jobExecuteExecutor;

    @PostMapping("/validate")
    public Response<?> validate(@RequestBody JobValidateDto dto) {
        String error = jobExecuteExecutor.validateParams(dto);
        if (StringUtils.hasText(error)) {
            return Response.error(ErrorCode.PARAM_INVALID.getCode(), error);
        }
        return Response.success();
    }
}
