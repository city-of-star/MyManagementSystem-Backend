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
 * 作用说明：
 * - 将当前 HTTP 请求中的【网关透传头】和【认证头】继续透传到 Feign 调用中
 * - 确保服务间调用时，下游服务仍然能通过 GatewaySignatureValidator 等组件完成安全校验
 * <p>
 * 设计要点：
 * - 不在下游服务重新生成签名，只是【透传】网关生成的签名和用户信息
 * - 当没有处于 HTTP 请求上下文（如定时任务）时，拦截器自动跳过
 *
 * 使用范围：
 * - 仅在业务服务（usercenter、base 等）中生效，网关未引入 web-mvc 模块，不会受到影响
 *
 * @author li.hongyu
 * @date 2025-12-30 17:47:37
 */
@Slf4j
@Component
public class GatewayHeaderRelayInterceptor implements RequestInterceptor {

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
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            // 不在 HTTP 请求上下文中（例如定时任务触发的 Feign 调用），跳过
            log.debug("Feign 透传拦截器：当前不在 HTTP 请求上下文中，跳过网关请求头透传");
            return;
        }

        HttpServletRequest request = attributes.getRequest();

        // 透传 Authorization 头（如有）
        String authHeader = request.getHeader(JwtConstants.Headers.AUTHORIZATION);
        if (StringUtils.hasText(authHeader)) {
            template.header(JwtConstants.Headers.AUTHORIZATION, authHeader);
            log.debug("Feign 透传拦截器：透传 Authorization 头");
        }

        // 透传网关相关请求头
        for (String headerName : GATEWAY_HEADERS) {
            String value = request.getHeader(headerName);
            if (StringUtils.hasText(value)) {
                template.header(headerName, value);
                log.debug("Feign 透传拦截器：透传请求头 {} = {}", headerName, value);
            }
        }
    }
}



