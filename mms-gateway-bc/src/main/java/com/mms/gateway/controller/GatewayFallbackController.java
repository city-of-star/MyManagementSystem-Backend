package com.mms.gateway.controller;

import com.mms.common.core.response.Response;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 实现功能【网关降级回退控制器】
 * <p>
 * 当下游服务超时或熔断打开时，统一返回友好错误信息。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-03-25 16:00:00
 */
@RestController
public class GatewayFallbackController {

    /**
     * 统一服务降级入口
     */
    @RequestMapping("/fallback/{service}")
    public Mono<Response<Object>> fallback(@PathVariable("service") String service) {
        return Mono.just(Response.error(503, service + " 服务繁忙，请稍后重试"));
    }
}
