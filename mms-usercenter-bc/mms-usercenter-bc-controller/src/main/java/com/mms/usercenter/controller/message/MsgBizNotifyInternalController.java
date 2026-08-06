package com.mms.usercenter.controller.message;

import com.mms.common.core.response.Response;
import com.mms.usercenter.common.message.dto.MsgBizNotifyDto;
import com.mms.usercenter.common.message.vo.MsgBizNotifyVo;
import com.mms.usercenter.service.message.service.MsgBizNotifyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 实现功能【业务系统通知内部接口】
 * <p>
 * 供其他服务（如定时任务）无登录态投递系统通知；须在白名单放行。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-06
 */
@Tag(name = "内部接口-业务系统通知", description = "服务间投递业务系统通知")
@RestController
@RequestMapping("/internal/msg")
public class MsgBizNotifyInternalController {

    @Resource
    private MsgBizNotifyService msgBizNotifyService;

    @Operation(summary = "投递业务系统通知")
    @PostMapping("/sys/notify")
    public Response<MsgBizNotifyVo> notify(@RequestBody @Valid MsgBizNotifyDto dto) {
        return Response.success(msgBizNotifyService.notify(dto));
    }
}
