package com.mms.common.security.feign;

import com.mms.common.core.constants.gateway.GatewayConstants;
import feign.Logger;
import feign.Request;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

/**
 * 实现功能【Feign 调用日志配置】
 * <p>
 * - 通过 Feign 自带的 Logger 记录【实际转发地址】和【耗时】
 * - 不自定义 Client，避免引入 Bean 循环依赖
 * </p>
 *
 * 日志示例：
 * Feign拦截器: 开始调用=GET http://base/test/1, traceId=xxx
 * Feign日志: 实际转发地址=http://192.168.1.10:38003/base/test/1, 状态码=200, 耗时=6 ms, traceId=xxx
 *
 * 仅在 Servlet 环境（业务服务）生效，网关（WebFlux）不加载。
 *
 * @author li.hongyu
 * @date 2026-01-06 16:20:54
 */
public class FeignLogger {

    /**
     * 开启 FULL 级别的 Feign 日志，这样 Feign 会调用自定义 Logger 的所有方法
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * 自定义 Feign Logger，用于记录实际转发地址和耗时
     */
    @Bean
    public Logger feignLogger() {
        return new FeignCallLogger();
    }

    /**
     * Feign 调用日志实现
     */
    @Slf4j
    static class FeignCallLogger extends Logger {

        @Override
        protected void log(String configKey, String format, Object... args) {
            // 统一走 SLF4J，避免使用父类的 System.out 打印
            log.debug(String.format(methodTag(configKey) + format, args));
        }

        @Override
        protected void logRequest(String configKey, Level logLevel, Request request) {
            // 这里的 URL 可能还是逻辑地址（经过负载均衡前），主要用于“开始调用”日志
            String method = request.httpMethod().name();
            String url = request.url();
            String traceId = MDC.get(GatewayConstants.Mdc.TRACE_ID);
            log.info("Feign日志: 开始调用={} {}, traceId={}",
                    method, url, traceId);
        }

        @Override
        protected Response logAndRebufferResponse(
                String configKey, Level logLevel, Response response, long elapsedTime) throws IOException {

            // 此时 response.request().url() 已是最终实际转发地址（包含 IP:Port）
            String url = response.request().url();
            int status = response.status();
            String traceId = MDC.get(GatewayConstants.Mdc.TRACE_ID);

            log.info("Feign日志: 实际转发地址={}, 状态码={}, 耗时={} ms, traceId={}",
                    url, status, elapsedTime, traceId);

            // 不修改响应体，直接返回即可
            return response;
        }
    }
}


