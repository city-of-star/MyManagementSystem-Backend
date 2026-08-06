package com.mms.usercenter.feign;

import com.mms.common.core.response.Response;
import com.mms.usercenter.common.message.dto.MsgBizNotifyDto;
import com.mms.usercenter.common.message.vo.MsgBizNotifyVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 实现功能【业务系统通知 Feign】
 * <p>
 * 直连 usercenter 内部接口，不经网关；调用方无需用户登录态。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-06
 */
@FeignClient(name = "usercenter", path = "/internal/msg")
public interface MsgBizNotifyFeign {

    /**
     * 投递业务系统通知
     */
    @PostMapping("/sys/notify")
    Response<MsgBizNotifyVo> notify(@RequestBody MsgBizNotifyDto dto);
}
