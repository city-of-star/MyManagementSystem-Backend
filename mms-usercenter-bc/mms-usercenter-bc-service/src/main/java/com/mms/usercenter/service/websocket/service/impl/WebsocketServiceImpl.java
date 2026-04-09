package com.mms.usercenter.service.websocket.service.impl;

import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.security.core.utils.JwtUtils;
import com.mms.common.security.core.utils.SessionUtils;
import com.mms.common.webmvc.utils.UserContextUtils;
import com.mms.usercenter.common.websocket.vo.WebsocketTokenVo;
import com.mms.usercenter.service.websocket.service.WebsocketService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 实现功能【Websocket 服务实现类】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-04-09 14:27:46
 */
@Service
public class WebsocketServiceImpl implements WebsocketService {

    /**
     * WebSocket 握手 token 默认有效期（秒）
     */
    private static final long WS_HANDSHAKE_TOKEN_TTL_SECONDS = 60L;

    @Resource
    private JwtUtils jwtUtils;

    @Resource
    private SessionUtils sessionUtils;

    /**
     * 获取 Websocket Token
     *
     * @return Websocket Token
     */
    @Override
    public WebsocketTokenVo getWsHandshakeToken() {
        // 获取用户ID和用户名
        Long userId = UserContextUtils.getUserId();
        String username = UserContextUtils.getUsername();
        if (userId == null || !StringUtils.hasText(username)) {
            throw new BusinessException(ErrorCode.LOGIN_EXPIRED);
        }
        // 严格单会话：WS token 必须绑定当前 sid，避免旧会话继续建立连接
        String sid = sessionUtils.getSessionId(username);
        if (!StringUtils.hasText(sid)) {
            throw new BusinessException(ErrorCode.LOGIN_EXPIRED);
        }
        // 生成 WebSocket 握手专用 Token
        String token = jwtUtils.generateWebSocketHandshakeToken(userId, username, sid, WS_HANDSHAKE_TOKEN_TTL_SECONDS);
        // 组装返回结果
        WebsocketTokenVo vo = new WebsocketTokenVo();
        vo.setWebsocketToken(token);
        vo.setWebsocketTokenExpiresIn(WS_HANDSHAKE_TOKEN_TTL_SECONDS);
        return vo;
    }
}