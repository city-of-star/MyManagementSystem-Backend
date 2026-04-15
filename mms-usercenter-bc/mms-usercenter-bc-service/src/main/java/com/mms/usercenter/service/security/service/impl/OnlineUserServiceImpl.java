package com.mms.usercenter.service.security.service.impl;

import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.utils.DateUtils;
import com.mms.common.security.core.utils.RefreshTokenUtils;
import com.mms.common.security.core.utils.SessionUtils;
import com.mms.common.websocket.constants.WebSocketConstants;
import com.mms.common.websocket.protocol.WsMessage;
import com.mms.common.websocket.service.WsPushService;
import com.mms.common.websocket.service.WsRegistryService;
import com.mms.usercenter.common.auth.entity.UserEntity;
import com.mms.usercenter.common.security.vo.OnlineUserVo;
import com.mms.usercenter.service.auth.mapper.UserMapper;
import com.mms.usercenter.service.security.service.OnlineUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.LocalDateTime;
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

    /**
     * 上一次在线用户会话数快照（userId -> sessionCount）
     * 用于在会话注册/注销时做增量对比，只推送变化用户，避免每次全量广播
     */
    private Map<String, Integer> lastOnlineCountMap = Collections.emptyMap();

    @Resource
    private UserMapper userMapper;

    @Resource
    private SessionUtils sessionUtils;

    @Resource
    private RefreshTokenUtils refreshTokenUtils;

    @Lazy
    @Resource
    private WsRegistryService wsRegistryService;

    @Resource
    private WsPushService wsPushService;

    /**
     * WebSocket 会话注册完成后触发
     */
    @Override
    public synchronized void onSessionRegistered() {
        pushDiff(lastOnlineCountMap, buildOnlineCountMap());
    }

    /**
     * WebSocket 会话注销完成后触发
     */
    @Override
    public synchronized void onSessionUnregistered() {
        pushDiff(lastOnlineCountMap, buildOnlineCountMap());
    }

    /**
     * 用户订阅在线用户房间后触发
     */
    @Override
    public synchronized void onOnlineUserRoomJoined() {
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

    /**
     * 查询在线用户列表
     */
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

    /**
     * 对比前后在线会话数快照并进行增量推送：
     * <ul>
     *     <li>新用户上线/会话数变化：推送 UPSERT</li>
     *     <li>用户全部离线：推送 REMOVE</li>
     * </ul>
     * 说明：
     * <ol>
     *     <li>before 是上一次快照，after 是当前快照；两者结构均为 userId -> sessionCount。</li>
     *     <li>先取并集，确保“新增用户”和“离线用户”都能被遍历到。</li>
     *     <li>只发送变化，避免每次连接变化都全量广播。</li>
     * </ol>
     */
    private void pushDiff(Map<String, Integer> before, Map<String, Integer> after) {
        // 取并集：任何在 before/after 出现过的用户，都可能需要推送变更。
        Set<String> userIds = new HashSet<>();
        userIds.addAll(before.keySet());
        userIds.addAll(after.keySet());
        // 无在线用户时仅更新基线，不发消息。
        if (userIds.isEmpty()) {
            lastOnlineCountMap = after;
            return;
        }
        // 预先加载用户信息，避免循环内重复查询。
        Map<String, UserEntity> userMap = loadUsers(userIds);
        for (String userId : userIds) {
            Integer oldCount = before.get(userId);
            Integer newCount = after.get(userId);
            // 新快照无该用户（或会话数<=0）且旧快照有在线记录：视为离线
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
            // 新上线或会话数变化时，推送最新用户在线信息
            if (oldCount == null || !oldCount.equals(newCount)) {
                pushToOnlineRoom(WsMessage.builder()
                        .type(TYPE_ONLINE_USER_UPSERT)
                        .data(toOnlineUserData(userId, newCount, userMap.get(userId)))
                        .timestamp(System.currentTimeMillis())
                        .build());
            }
        }
        // 更新基线：下一次 diff 会基于这次结果继续比较。
        lastOnlineCountMap = after;
    }

    /**
     * 从注册表中统计当前在线会话数（userId -> sessionCount）。
     * <p>
     * 统计口径：
     * <ul>
     *     <li>仅统计仍处于 open 状态的 WebSocketSession。</li>
     *     <li>仅统计 attributes 中存在合法 WS_USER_ID 的会话。</li>
     *     <li>同一 userId 多连接会累计（多端登录场景）。</li>
     * </ul>
     * </p>
     */
    private Map<String, Integer> buildOnlineCountMap() {
        Set<WebSocketSession> sessions = wsRegistryService.getAllSessions();
        if (sessions.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Integer> countMap = new HashMap<>();
        for (WebSocketSession session : sessions) {
            // 跳过空会话或已关闭会话，防止脏数据影响统计。
            if (session == null || !session.isOpen()) {
                continue;
            }
            Object userIdAttr = session.getAttributes().get(WebSocketConstants.WS_USER_ID);
            String userId = userIdAttr == null ? "" : String.valueOf(userIdAttr).trim();
            // 未鉴权/无 userId 的连接不计入在线用户。
            if (userId.isEmpty()) {
                continue;
            }
            // 同一用户多个连接累计计数。
            countMap.merge(userId, 1, Integer::sum);
        }
        return countMap;
    }

    /**
     * 批量加载用户基础信息。
     * <p>
     * 这里按 userId 循环查询，优先保证逻辑稳定；非法 userId 会被跳过。
     * </p>
     */
    private Map<String, UserEntity> loadUsers(Collection<String> userIds) {
        Map<String, UserEntity> result = new HashMap<>();
        for (String userId : userIds) {
            try {
                // userId 源于 WebSocket attributes，先做安全转换。
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
     * 组装 WebSocket 推送使用的在线用户数据结构。
     * <p>
     * 即使用户基础信息缺失，也会至少返回 userId + sessionCount，保证前端可落盘/删改。
     * </p>
     */
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
            data.put("loginTime", DateUtils.formatDateTime(loginTime));
            data.put("lastActiveTime", DateUtils.formatDateTime(loginTime));
        }
        return data;
    }

    /**
     * 组装接口返回的在线用户视图对象。
     * <p>
     * 与推送结构不同，这里返回的是强类型 VO，便于后端接口文档和前端类型约束。
     * </p>
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
     * 推送消息到在线用户房间。
     * <p>
     * 统一封装房间常量，避免业务代码散落硬编码。
     * </p>
     */
    private void pushToOnlineRoom(WsMessage<?> message) {
        wsPushService.pushToRoom(ROOM_ONLINE_USER, message);
    }
}