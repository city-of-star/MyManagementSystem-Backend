package com.mms.common.security.feign;

import com.mms.common.core.constants.gateway.GatewayConstants;
import com.mms.common.security.constants.JwtConstants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
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

        // 检查是否有用户信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            // 目前暂无【定时任务触发的 Feign 调用】的需求
            // 后期如果有这个需求，可以在此处使用 HMAC 加一个凭证
            // 然后下游服务器在发现没有网关签名的时候再验证这个凭证就好了
            return;
        }

        HttpServletRequest request = attributes.getRequest();

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
            }
        }
    }
}



