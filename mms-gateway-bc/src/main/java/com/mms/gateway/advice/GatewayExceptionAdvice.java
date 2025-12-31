package com.mms.gateway.advice;

import com.mms.common.core.constants.gateway.GatewayConstants;
import com.mms.gateway.utils.GatewayExceptionUtils;
import com.mms.gateway.utils.GatewayResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 实现功能【网关全局异常处理器 - WebFlux】
 * <p>
 * - 仅作用于 Spring Cloud Gateway（WebFlux），不影响各业务服务的 MVC 全局异常处理
 * - 统一返回 common 的 Response 结构，并携带 traceId
 * <p>
 *
 * @author li.hongyu
 * @date 2025-11-11 20:41:36
 */
@Slf4j
@Component
public class GatewayExceptionAdvice implements ErrorWebExceptionHandler, Ordered {

	@Override
	public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
		ServerHttpRequest request = exchange.getRequest();
		String path = request.getURI().getPath();
		String method = request.getMethod().name();
		
		// 根据异常类型解析对应的 HTTP 状态码
		HttpStatus status = GatewayExceptionUtils.resolveHttpStatus(ex);
		// 根据异常和状态码解析用户友好的错误消息
		String message = GatewayExceptionUtils.resolveMessage(ex, status);

		// 根据异常类型和状态码决定日志级别
		if (status.is5xxServerError()) {
			// 5xx错误记录ERROR级别，包含完整堆栈
			log.error("网关异常: {} {} - status={}, message={}, exception={}", 
					method, path, status.value(), message, ex.getClass().getSimpleName(), ex);
		} else if (status == HttpStatus.BAD_GATEWAY || status == HttpStatus.GATEWAY_TIMEOUT) {
			// 网关相关错误（502、504）记录WARN级别
			log.warn("网关异常: {} {} - status={}, message={}, exception={}", 
					method, path, status.value(), message, ex.getClass().getSimpleName());
		}

		// 使用统一的响应工具写入错误响应
		return GatewayResponseUtils.writeError(exchange, status, message);
	}

	@Override
	public int getOrder() {
		// 设置为最高优先级，确保最先执行
		return GatewayConstants.FilterOrder.GLOBAL_EXCEPTION_HANDLER;
	}
}


