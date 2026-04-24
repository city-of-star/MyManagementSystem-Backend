package com.mms.usercenter.service.security.service.impl;

import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.security.core.utils.RefreshTokenUtils;
import com.mms.common.security.core.utils.SessionUtils;
import com.mms.common.websocket.registry.service.WsRegistryService;
import com.mms.usercenter.common.auth.entity.UserEntity;
import com.mms.usercenter.common.security.vo.OnlineUserVo;
import com.mms.usercenter.service.auth.mapper.UserMapper;
import com.mms.usercenter.service.security.service.OnlineUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * 实现功能【在线用户服务实现类】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-04-03 14:45:41
 */
@Slf4j
@Service
public class OnlineUserServiceImpl implements OnlineUserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private SessionUtils sessionUtils;

    @Resource
    private RefreshTokenUtils refreshTokenUtils;

    @Resource
    private WsRegistryService wsRegistryService;

    @Resource
    private OnlineUserWsRegistryListener onlineUserWsRegistryListener;

    /**
     * 查询在线用户列表
     */
    @Override
    public synchronized List<OnlineUserVo> getOnlineUsers() {
        return onlineUserWsRegistryListener.getOnlineUsersInternal();
    }

    /**
     * 强制指定用户下线
     */
    @Override
    public synchronized void forceLogout(Long userId) {
        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        String userIdKey = String.valueOf(userId);
        // 查询该用户的会话集合
        Set<WebSocketSession> sessions = wsRegistryService.getByUserId(userIdKey);
        // 强制下线关闭会话
        if (sessions != null && !sessions.isEmpty()) {
            for (WebSocketSession session : sessions) {
                if (session == null || !session.isOpen()) {
                    continue;
                }
                try {
                    session.close(new CloseStatus(4001, "管理员强制下线关闭会话"));
                } catch (IOException e) {
                    log.warn("强制下线关闭会话失败, sessionId={}", session.getId(), e);
                }
            }
        }
        // 移除 Redis 中的 sessionId 和 refreshToken
        if (StringUtils.hasText(user.getUsername())) {
            sessionUtils.removeSessionId(user.getUsername());
            refreshTokenUtils.removeRefreshToken(user.getUsername());
        }
    }

}