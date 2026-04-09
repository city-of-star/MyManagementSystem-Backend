package com.mms.usercenter.controller.websocket;

import com.mms.common.core.response.Response;
import com.mms.usercenter.common.websocket.vo.WebsocketTokenVo;
import com.mms.usercenter.service.websocket.service.WebsocketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 实现功能【Websocket Controller】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-04-09 14:25:39
 */
@RestController
@RequestMapping("/ws")
@RequiredArgsConstructor
@Tag(name = "Websocket 服务", description = "Websocket 服务")
public class WebsocketController {

    private final WebsocketService websocketService;

    @Operation(summary = "根据用户名获取角色与权限")
    @GetMapping("/handshake-token")
    public Response<WebsocketTokenVo> getWsHandshakeToken() {
        return Response.success(websocketService.getWsHandshakeToken());
    }

}