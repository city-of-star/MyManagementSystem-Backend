package com.mms.gateway.config;

import com.mms.common.core.constants.gateway.GatewayConstants;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

/**
 * 实现功能【网关限流配置】
 * <p>
 * 限流键优先使用用户ID，未登录请求回退为客户端IP。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-03-25 16:00:00
 */
@Configuration
public class RateLimitConfig {

    /**
     * 限流 key 解析器（优先用户ID，其次客户端IP）
     */
    @Bean("ipOrUserKeyResolver")
    public KeyResolver ipOrUserKeyResolver() {
        return exchange -> {
            String userId = exchange.getRequest().getHeaders().getFirst(GatewayConstants.Headers.USER_ID);
            if (StringUtils.hasText(userId)) {
                return Mono.just("u:" + userId);
            }

            InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
            String ip = (remoteAddress != null && remoteAddress.getAddress() != null)
                    ? remoteAddress.getAddress().getHostAddress()
                    : "unknown";
            return Mono.just("ip:" + ip);
        };
    }
}
