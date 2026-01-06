package com.mms.common.web.feign;

import com.mms.common.core.constants.gateway.GatewayConstants;
import com.mms.common.security.constants.JwtConstants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;

/**
 * 实现功能【Feign 请求头透传拦截器】
 * <p>
 * 将当前 HTTP 请求中的【网关透传头】和【认证头】继续透传到 Feign 调用中
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-30 17:47:37
 */
@Slf4j
@Component
public class FeignHeaderRelayInterceptor implements RequestInterceptor {

    /**
     * 需要透传的网关相关请求头列表
     */
    private static final List<String> GATEWAY_HEADERS = Arrays.asList(
            GatewayConstants.Headers.TRACE_ID,
            GatewayConstants.Headers.USER_ID,
            GatewayConstants.Headers.USER_NAME,
            GatewayConstants.Headers.TOKEN_JTI,
            GatewayConstants.Headers.TOKEN_EXP,
            GatewayConstants.Headers.CLIENT_IP,
            GatewayConstants.Headers.USER_AGENT,
            GatewayConstants.Headers.LOGIN_LOCATION,
            GatewayConstants.Headers.GATEWAY_SIGNATURE,
            GatewayConstants.Headers.GATEWAY_TIMESTAMP
    );

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            // 不在 HTTP 请求上下文中（例如定时任务触发的 Feign 调用），跳过
            log.info("Feign请求头透传：不在HTTP请求上下文，跳过 | 目标: {} {}", 
                    template.method(), template.url());
            return;
        }

        HttpServletRequest request = attributes.getRequest();
        String targetUrl = template.method() + " " + template.url();
        
        // 统计透传的头信息
        String traceId = null;

        // 透传 Authorization 头
        String authHeader = request.getHeader(JwtConstants.Headers.AUTHORIZATION);
        if (StringUtils.hasText(authHeader)) {
            template.header(JwtConstants.Headers.AUTHORIZATION, authHeader);
        }

        // 透传网关相关请求头
        for (String headerName : GATEWAY_HEADERS) {
            String value = request.getHeader(headerName);
            if (StringUtils.hasText(value)) {
                template.header(headerName, value);
                
                // 记录关键头信息用于排查
                if (GatewayConstants.Headers.TRACE_ID.equals(headerName)) {
                    traceId = value;
                }
            }
        }

        // 记录透传摘要信息
        StringBuilder logMsg = new StringBuilder("Feign请求头透传 | 目标: ").append(targetUrl);
        if (traceId != null) {
            logMsg.append(" | TraceId: ").append(traceId);
        }
        log.info(logMsg.toString());
    }
}



