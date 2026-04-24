package com.mms.usercenter.service.security.service.impl;

import com.mms.common.core.utils.DateUtils;
import com.mms.common.websocket.common.constants.WebSocketConstants;
import com.mms.common.websocket.common.protocol.WsMessage;
import com.mms.common.websocket.push.service.WsPushService;
import com.mms.common.websocket.registry.event.WsRoomJoinedEvent;
import com.mms.common.websocket.registry.event.WsSessionRegisteredEvent;
import com.mms.common.websocket.registry.event.WsSessionUnregisteredEvent;
import com.mms.common.websocket.registry.service.WsRegistryService;
import com.mms.usercenter.common.auth.entity.UserEntity;
import com.mms.usercenter.common.security.constants.OnlineUserConstants;
import com.mms.usercenter.common.security.event.OnlineUserFullEvent;
import com.mms.usercenter.common.security.event.OnlineUserRemoveEvent;
import com.mms.usercenter.common.security.event.OnlineUserUpsertEvent;
import com.mms.usercenter.common.security.vo.OnlineUserVo;
import com.mms.usercenter.service.auth.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 在线用户 WS 生命周期业务处理器（消费事件并执行业务逻辑）。
 */
@Component
public class OnlineUserWsLifecycleHandler {

    @Resource
    private UserMapper userMapper;

    @Resource
    private WsRegistryService wsRegistryService;

    @Resource
    private WsPushService wsPushService;

    /**
     * 上一次在线用户会话数快照（userId -> sessionCount）
     * 用于在会话注册/注销时做增量对比，只推送变化用户，避免每次全量广播
     */
    private Map<String, Integer> lastOnlineCountMap = Collections.emptyMap();

    /**
     * WebSocket 会话注册完成后触发
     */
    @EventListener
    public synchronized void onSessionRegistered(WsSessionRegisteredEvent event) {
        pushDiff(lastOnlineCountMap, buildOnlineCountMap());
    }

    /**
     * WebSocket 会话注销完成后触发
     */
    @EventListener
    public synchronized void onSessionUnregistered(WsSessionUnregisteredEvent event) {
        pushDiff(lastOnlineCountMap, buildOnlineCountMap());
    }

    /**
     * 用户订阅在线用户房间后触发
     */
    @EventListener
    public synchronized void onRoomJoined(WsRoomJoinedEvent event) {
        if (!OnlineUserConstants.ROOM_ONLINE_USER.equals(event.getRoomId())) {
            return;
        }
        // 统计当前在线会话数（userId -> 该userId的会话数）
        Map<String, Integer> countMap = buildOnlineCountMap();
        // 加载用户的信息（userId -> 用户实体信息）
        Map<String, UserEntity> userMap = loadUsers(countMap.keySet());
        // 拼接成在线用户列表（ws推送使用）
        List<OnlineUserUpsertEvent> users = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
            users.add(toOnlineUserUpsertEvent(entry.getKey(), entry.getValue(), userMap.get(entry.getKey())));
        }
        // 全量推送消息
        pushToOnlineRoom(WsMessage.builder()
                .type(OnlineUserConstants.TYPE_ONLINE_USER_FULL)
                .data(new OnlineUserFullEvent(users))
                .timestamp(DateUtils.nowMillis())
                .build());
        // 保存最新在线用户会话数快照
        lastOnlineCountMap = countMap;
    }

    /**
     * 查询在线用户列表
     */
    public synchronized List<OnlineUserVo> getOnlineUsers() {
        // 统计当前在线会话数（userId -> 该userId的会话数）
        Map<String, Integer> countMap = buildOnlineCountMap();
        // 加载用户的信息（userId -> 用户实体信息）
        Map<String, UserEntity> userMap = loadUsers(countMap.keySet());
        // 拼接成在线用户列表（http响应使用）
        List<OnlineUserVo> list = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
            String userId = entry.getKey();
            list.add(toOnlineUserVo(userId, entry.getValue(), userMap.get(userId)));
        }
        // 排序
        list.sort(
                Comparator.comparing(OnlineUserVo::getSessionCount, Comparator.nullsLast(Integer::compareTo)).reversed()
                        .thenComparing(OnlineUserVo::getUserId, Comparator.nullsLast(Long::compareTo))
        );
        return list;
    }

    /**
     * 对比前后在线会话数快照并进行增量推送
     */
    private void pushDiff(Map<String, Integer> before, Map<String, Integer> after) {
        // 取并集：任何在 before/after 出现过的用户，都可能需要推送变更
        Set<String> userIds = new HashSet<>();
        userIds.addAll(before.keySet());
        userIds.addAll(after.keySet());
        // 无在线用户时仅更新基线，不发消息
        if (userIds.isEmpty()) {
            lastOnlineCountMap = after;
            return;
        }
        // 预先加载用户信息，避免循环内重复查询
        Map<String, UserEntity> userMap = loadUsers(userIds);
        for (String userId : userIds) {
            // 该用户旧的会话数量
            Integer oldCount = before.get(userId);
            // 该用户新的会话数量
            Integer newCount = after.get(userId);
            // 新快照无该用户（或会话数<=0）且旧快照有在线记录：视为离线
            if (newCount == null || newCount <= 0) {
                if (oldCount != null && oldCount > 0) {
                    pushToOnlineRoom(WsMessage.builder()
                            .type(OnlineUserConstants.TYPE_ONLINE_USER_REMOVE)
                            .data(new OnlineUserRemoveEvent(userId))
                            .timestamp(DateUtils.nowMillis())
                            .build());
                }
                continue;
            }
            // 新上线或会话数变化时，推送最新用户在线信息
            if (oldCount == null || !oldCount.equals(newCount)) {
                pushToOnlineRoom(WsMessage.builder()
                        .type(OnlineUserConstants.TYPE_ONLINE_USER_UPSERT)
                        .data(toOnlineUserUpsertEvent(userId, newCount, userMap.get(userId)))
                        .timestamp(DateUtils.nowMillis())
                        .build());
            }
        }
        // 保存最新在线用户会话数快照
        lastOnlineCountMap = after;
    }

    /**
     * 从注册表中统计当前在线会话数
     */
    private Map<String, Integer> buildOnlineCountMap() {
        Set<WebSocketSession> sessions = wsRegistryService.getAllSessions();
        if (sessions.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Integer> countMap = new HashMap<>();
        for (WebSocketSession session : sessions) {
            // 跳过空会话或已关闭会话，防止脏数据影响统计
            if (session == null || !session.isOpen()) {
                continue;
            }
            Object userIdAttr = session.getAttributes().get(WebSocketConstants.WS_USER_ID);
            String userId = userIdAttr == null ? "" : String.valueOf(userIdAttr).trim();
            // 未鉴权/无 userId 的连接不计入在线用户
            if (userId.isEmpty()) {
                continue;
            }
            // 同一用户多个连接累计计数
            countMap.merge(userId, 1, Integer::sum);
        }
        return countMap;
    }

    /**
     * 批量加载用户基础信息
     */
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

    /**
     * 组装 WebSocket 推送使用的在线用户新增/更新事件
     */
    private OnlineUserUpsertEvent toOnlineUserUpsertEvent(String userId, Integer sessionCount, UserEntity user) {
        OnlineUserUpsertEvent event = new OnlineUserUpsertEvent();
        event.setUserId(userId);
        event.setSessionCount(sessionCount);
        if (user == null) {
            return event;
        }
        event.setUsername(user.getUsername());
        event.setNickname(user.getNickname());
        event.setRealName(user.getRealName());
        event.setLoginIp(user.getLastLoginIp());
        LocalDateTime loginTime = user.getLastLoginTime();
        if (loginTime != null) {
            String text = DateUtils.formatDateTime(loginTime);
            event.setLoginTime(text);
            event.setLastActiveTime(text);
        }
        return event;
    }

    /**
     * 组装接口返回的在线用户视图对象
     */
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
            String text = DateUtils.formatDateTime(loginTime);
            vo.setLoginTime(text);
            vo.setLastActiveTime(text);
        }
        return vo;
    }

    /**
     * 推送消息到在线用户房间
     */
    private void pushToOnlineRoom(WsMessage<?> message) {
        wsPushService.pushToRoom(OnlineUserConstants.ROOM_ONLINE_USER, message);
    }
}
