package com.mms.usercenter.service.security.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.security.core.utils.RefreshTokenUtils;
import com.mms.common.security.core.utils.SessionUtils;
import com.mms.common.websocket.constants.WebSocketConstants;
import com.mms.common.websocket.protocol.WsMessage;
import com.mms.common.websocket.service.WsRegistryService;
import com.mms.usercenter.common.auth.entity.UserEntity;
import com.mms.usercenter.common.security.vo.OnlineUserVo;
import com.mms.usercenter.service.auth.mapper.UserMapper;
import com.mms.usercenter.service.security.service.OnlineUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Comparator;

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

    private static final String ROOM_ONLINE_USER = "security_online_user";
    private static final String TYPE_ONLINE_USER_FULL = "online_user_full";
    private static final String TYPE_ONLINE_USER_UPSERT = "online_user_upsert";
    private static final String TYPE_ONLINE_USER_REMOVE = "online_user_remove";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;
    private final SessionUtils sessionUtils;
    private final RefreshTokenUtils refreshTokenUtils;
    private final WsRegistryService wsRegistryService;
    private Map<String, Integer> lastOnlineCountMap = Collections.emptyMap();

    public OnlineUserServiceImpl(UserMapper userMapper,
                                 ObjectMapper objectMapper,
                                 SessionUtils sessionUtils,
                                 RefreshTokenUtils refreshTokenUtils,
                                 @Lazy WsRegistryService wsRegistryService) {
        this.userMapper = userMapper;
        this.objectMapper = objectMapper;
        this.sessionUtils = sessionUtils;
        this.refreshTokenUtils = refreshTokenUtils;
        this.wsRegistryService = wsRegistryService;
    }

    @Override
    public synchronized void onSessionRegistered() {
        pushDiff(lastOnlineCountMap, buildOnlineCountMap());
    }

    @Override
    public synchronized void onSessionUnregistered() {
        pushDiff(lastOnlineCountMap, buildOnlineCountMap());
    }

    @Override
    public synchronized void onOnlineUserRoomJoined() {
        pushFullSnapshot();
    }

    @Override
    public synchronized List<OnlineUserVo> getOnlineUsers() {
        Map<String, Integer> countMap = buildOnlineCountMap();
        Map<String, UserEntity> userMap = loadUsers(countMap.keySet());
        List<OnlineUserVo> list = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
            String userId = entry.getKey();
            list.add(toOnlineUserVo(userId, entry.getValue(), userMap.get(userId)));
        }
        list.sort(
                Comparator.comparing(OnlineUserVo::getSessionCount, Comparator.nullsLast(Integer::compareTo)).reversed()
                        .thenComparing(OnlineUserVo::getUserId, Comparator.nullsLast(Long::compareTo))
        );
        return list;
    }

    @Override
    public synchronized void forceLogout(Long userId) {
        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        String userIdKey = String.valueOf(userId);
        Set<WebSocketSession> sessions = wsRegistryService.getByUserId(userIdKey);
        if (sessions != null && !sessions.isEmpty()) {
            for (WebSocketSession session : sessions) {
                if (session == null || !session.isOpen()) {
                    continue;
                }
                try {
                    session.close(new CloseStatus(4001, "force-logout"));
                } catch (IOException e) {
                    log.warn("强制下线关闭会话失败, sessionId={}", session.getId(), e);
                }
            }
        }
        if (StringUtils.hasText(user.getUsername())) {
            sessionUtils.removeSessionId(user.getUsername());
            refreshTokenUtils.removeRefreshToken(user.getUsername());
        }
    }

    private void pushDiff(Map<String, Integer> before, Map<String, Integer> after) {
        Set<String> userIds = new HashSet<>();
        userIds.addAll(before.keySet());
        userIds.addAll(after.keySet());
        if (userIds.isEmpty()) {
            lastOnlineCountMap = after;
            return;
        }
        Map<String, UserEntity> userMap = loadUsers(userIds);
        for (String userId : userIds) {
            Integer oldCount = before.get(userId);
            Integer newCount = after.get(userId);
            if (newCount == null || newCount <= 0) {
                if (oldCount != null && oldCount > 0) {
                    pushToOnlineRoom(WsMessage.builder()
                            .type(TYPE_ONLINE_USER_REMOVE)
                            .data(Map.of("userId", userId))
                            .timestamp(System.currentTimeMillis())
                            .build());
                }
                continue;
            }
            if (oldCount == null || !oldCount.equals(newCount)) {
                pushToOnlineRoom(WsMessage.builder()
                        .type(TYPE_ONLINE_USER_UPSERT)
                        .data(toOnlineUserData(userId, newCount, userMap.get(userId)))
                        .timestamp(System.currentTimeMillis())
                        .build());
            }
        }
        lastOnlineCountMap = after;
    }

    private void pushFullSnapshot() {
        Map<String, Integer> countMap = buildOnlineCountMap();
        Map<String, UserEntity> userMap = loadUsers(countMap.keySet());
        List<Map<String, Object>> users = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
            users.add(toOnlineUserData(entry.getKey(), entry.getValue(), userMap.get(entry.getKey())));
        }
        pushToOnlineRoom(WsMessage.builder()
                .type(TYPE_ONLINE_USER_FULL)
                .data(users)
                .timestamp(System.currentTimeMillis())
                .build());
        lastOnlineCountMap = countMap;
    }

    private Map<String, Integer> buildOnlineCountMap() {
        Set<WebSocketSession> sessions = wsRegistryService.getAllSessions();
        if (sessions.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Integer> countMap = new HashMap<>();
        for (WebSocketSession session : sessions) {
            if (session == null || !session.isOpen()) {
                continue;
            }
            Object userIdAttr = session.getAttributes().get(WebSocketConstants.ATTR_USER_ID);
            String userId = userIdAttr == null ? "" : String.valueOf(userIdAttr).trim();
            if (userId.isEmpty()) {
                continue;
            }
            countMap.merge(userId, 1, Integer::sum);
        }
        return countMap;
    }

    private Map<String, UserEntity> loadUsers(Collection<String> userIds) {
        Map<String, UserEntity> result = new HashMap<>();
        for (String userId : userIds) {
            try {
                Long id = Long.valueOf(userId);
                UserEntity entity = userMapper.selectById(id);
                if (entity != null) {
                    result.put(userId, entity);
                }
            } catch (Exception ignored) {
                // 用户ID格式异常或数据不存在时，继续处理其它用户
            }
        }
        return result;
    }

    private Map<String, Object> toOnlineUserData(String userId, Integer sessionCount, UserEntity user) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("sessionCount", sessionCount);
        if (user == null) {
            return data;
        }
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());
        data.put("realName", user.getRealName());
        data.put("loginIp", user.getLastLoginIp());
        LocalDateTime loginTime = user.getLastLoginTime();
        if (loginTime != null) {
            data.put("loginTime", DATE_TIME_FORMATTER.format(loginTime));
            data.put("lastActiveTime", DATE_TIME_FORMATTER.format(loginTime));
        }
        return data;
    }

    private OnlineUserVo toOnlineUserVo(String userId, Integer sessionCount, UserEntity user) {
        OnlineUserVo vo = new OnlineUserVo();
        try {
            vo.setUserId(Long.valueOf(userId));
        } catch (Exception ignored) {
            vo.setUserId(null);
        }
        vo.setSessionCount(sessionCount);
        if (user == null) {
            return vo;
        }
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setRealName(user.getRealName());
        vo.setLoginIp(user.getLastLoginIp());
        LocalDateTime loginTime = user.getLastLoginTime();
        if (loginTime != null) {
            String text = DATE_TIME_FORMATTER.format(loginTime);
            vo.setLoginTime(text);
            vo.setLastActiveTime(text);
        }
        return vo;
    }

    private void pushToOnlineRoom(WsMessage<?> message) {
        Set<WebSocketSession> sessions = wsRegistryService.getByRoomId(ROOM_ONLINE_USER);
        if (sessions == null || sessions.isEmpty()) {
            return;
        }
        final String payload;
        try {
            payload = objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            log.warn("在线用户消息序列化失败", e);
            return;
        }
        for (WebSocketSession session : sessions) {
            if (session == null || !session.isOpen()) {
                continue;
            }
            try {
                session.sendMessage(new TextMessage(payload));
            } catch (IOException e) {
                log.warn("在线用户消息推送失败, sessionId={}", session.getId(), e);
            }
        }
    }
}