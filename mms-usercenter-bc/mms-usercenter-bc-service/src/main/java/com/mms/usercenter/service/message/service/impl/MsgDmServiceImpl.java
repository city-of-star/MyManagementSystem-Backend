package com.mms.usercenter.service.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.system.vo.AttachmentVo;
import com.mms.base.feign.AttachmentFeign;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.response.Response;
import com.mms.common.webmvc.utils.UserContextUtils;
import com.mms.usercenter.common.auth.entity.UserEntity;
import com.mms.usercenter.common.message.constants.MsgConstants;
import com.mms.usercenter.common.message.dto.MsgDmConversationPageQueryDto;
import com.mms.usercenter.common.message.dto.MsgDmMessagePageQueryDto;
import com.mms.usercenter.common.message.dto.MsgDmOpenDto;
import com.mms.usercenter.common.message.dto.MsgDmSendDto;
import com.mms.usercenter.common.message.dto.MsgDmUserSearchDto;
import com.mms.usercenter.common.message.entity.MsgDmConversationEntity;
import com.mms.usercenter.common.message.entity.MsgDmMemberEntity;
import com.mms.usercenter.common.message.entity.MsgDmMessageEntity;
import com.mms.usercenter.common.message.vo.MsgDmConversationVo;
import com.mms.usercenter.common.message.vo.MsgDmMessageVo;
import com.mms.usercenter.common.message.vo.MsgDmUserVo;
import com.mms.usercenter.service.auth.mapper.UserMapper;
import com.mms.usercenter.service.message.mapper.MsgDmConversationMapper;
import com.mms.usercenter.service.message.mapper.MsgDmMemberMapper;
import com.mms.usercenter.service.message.mapper.MsgDmMessageMapper;
import com.mms.usercenter.service.message.service.MsgDmService;
import com.mms.usercenter.service.message.support.MsgUnreadSupport;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 实现功能【私信服务实现】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
@Slf4j
@Service
public class MsgDmServiceImpl implements MsgDmService {

    private static final Executor AVATAR_RESOLVE_EXECUTOR = Executors.newFixedThreadPool(4, r -> {
        Thread t = new Thread(r, "msg-dm-avatar");
        t.setDaemon(true);
        return t;
    });

    @Resource
    private MsgDmConversationMapper msgDmConversationMapper;

    @Resource
    private MsgDmMemberMapper msgDmMemberMapper;

    @Resource
    private MsgDmMessageMapper msgDmMessageMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private MsgUnreadSupport msgUnreadSupport;

    @Resource
    private AttachmentFeign attachmentFeign;

    @Override
    public Page<MsgDmConversationVo> getConversationPage(MsgDmConversationPageQueryDto dto) {
        Long userId = requireUserId();
        int pageNum = dto.getPageNum() == null || dto.getPageNum() < 1 ? 1 : dto.getPageNum();
        int pageSize = dto.getPageSize() == null || dto.getPageSize() < 1 ? 20 : dto.getPageSize();
        String sortMode = MsgConstants.SORT_TIME.equalsIgnoreCase(dto.getSortMode())
                ? MsgConstants.SORT_TIME
                : MsgConstants.SORT_UNREAD;

        Page<MsgDmMemberEntity> memberPage = msgDmMemberMapper.pageConversations(
                new Page<>(pageNum, pageSize),
                userId,
                StringUtils.hasText(dto.getKeyword()) ? dto.getKeyword().trim() : null,
                sortMode);

        List<MsgDmMemberEntity> members = memberPage.getRecords();
        Set<Long> peerIds = members.stream().map(MsgDmMemberEntity::getPeerId).collect(Collectors.toSet());
        Map<Long, UserEntity> peerMap = peerIds.isEmpty() ? Map.of()
                : userMapper.selectBatchIds(peerIds).stream().collect(Collectors.toMap(UserEntity::getId, u -> u, (a, b) -> a));

        Set<Long> avatarIds = new HashSet<>();
        for (UserEntity peer : peerMap.values()) {
            if (peer.getAvatarId() != null) {
                avatarIds.add(peer.getAvatarId());
            }
        }
        Map<Long, String> avatarUrlCache = resolveAvatarUrls(avatarIds);

        List<MsgDmConversationVo> records = new ArrayList<>(members.size());
        for (MsgDmMemberEntity member : members) {
            records.add(toConversationVo(member, peerMap.get(member.getPeerId()), avatarUrlCache));
        }
        Page<MsgDmConversationVo> page = new Page<>(memberPage.getCurrent(), memberPage.getSize(), memberPage.getTotal());
        page.setRecords(records);
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MsgDmConversationVo openConversation(MsgDmOpenDto dto) {
        Long userId = requireUserId();
        Long peerId = dto.getPeerUserId();
        if (Objects.equals(userId, peerId)) {
            throw new BusinessException("不能与自己发起私信");
        }
        UserEntity peer = userMapper.selectById(peerId);
        if (peer == null || peer.getStatus() == null || peer.getStatus() != 1) {
            throw new BusinessException("对方账号不可用");
        }
        MsgDmConversationEntity conversation = getOrCreateConversation(userId, peerId);
        // 自己侧可见；对方侧默认 hidden=1，等首条消息再露出，避免空会话污染对方列表
        MsgDmMemberEntity self = getOrCreateMember(conversation.getId(), userId, peerId, 0);
        getOrCreateMember(conversation.getId(), peerId, userId, 1);
        if (self.getHidden() != null && self.getHidden() == 1) {
            self.setHidden(0);
            msgDmMemberMapper.updateById(self);
        }
        return toConversationVo(self, peer, resolveAvatarUrls(
                peer.getAvatarId() == null ? Set.of() : Set.of(peer.getAvatarId())));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MsgDmConversationVo getConversationById(Long conversationId) {
        Long userId = requireUserId();
        MsgDmMemberEntity self = requireMember(conversationId, userId);
        if (self.getHidden() != null && self.getHidden() == 1) {
            self.setHidden(0);
            msgDmMemberMapper.updateById(self);
        }
        UserEntity peer = userMapper.selectById(self.getPeerId());
        Set<Long> avatarIds = peer != null && peer.getAvatarId() != null ? Set.of(peer.getAvatarId()) : Set.of();
        return toConversationVo(self, peer, resolveAvatarUrls(avatarIds));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Page<MsgDmMessageVo> getMessagePage(MsgDmMessagePageQueryDto dto) {
        Long userId = requireUserId();
        if (dto.getConversationId() == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "会话ID不能为空");
        }
        MsgDmMemberEntity self = requireMember(dto.getConversationId(), userId);
        int pageNum = dto.getPageNum() == null || dto.getPageNum() < 1 ? 1 : dto.getPageNum();
        int pageSize = dto.getPageSize() == null || dto.getPageSize() < 1 ? 20 : Math.min(dto.getPageSize(), 100);
        long clearedBefore = self.getClearedBeforeId() == null ? 0L : self.getClearedBeforeId();
        boolean cursorMode = dto.getBeforeId() != null && dto.getBeforeId() > 0;

        LambdaQueryWrapper<MsgDmMessageEntity> wrapper = new LambdaQueryWrapper<MsgDmMessageEntity>()
                .eq(MsgDmMessageEntity::getConversationId, dto.getConversationId())
                .gt(MsgDmMessageEntity::getId, clearedBefore)
                .lt(cursorMode, MsgDmMessageEntity::getId, dto.getBeforeId())
                .orderByDesc(MsgDmMessageEntity::getId);
        Page<MsgDmMessageEntity> page = msgDmMessageMapper.selectPage(
                new Page<>(cursorMode ? 1 : pageNum, pageSize), wrapper);

        if (!cursorMode) {
            Long maxId = page.getRecords().stream().map(MsgDmMessageEntity::getId).max(Long::compareTo).orElse(null);
            boolean changed = false;
            if (maxId != null && (self.getLastReadMsgId() == null || maxId > self.getLastReadMsgId())) {
                self.setLastReadMsgId(maxId);
                changed = true;
            }
            if (self.getUnreadCount() != null && self.getUnreadCount() > 0) {
                self.setUnreadCount(0);
                changed = true;
            }
            if (self.getHidden() != null && self.getHidden() == 1) {
                self.setHidden(0);
                changed = true;
            }
            if (changed) {
                msgDmMemberMapper.updateById(self);
                msgUnreadSupport.pushUnread(userId);
            }
        }

        Page<MsgDmMessageVo> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<MsgDmMessageVo> records = page.getRecords().stream().map(msg -> {
            MsgDmMessageVo vo = new MsgDmMessageVo();
            BeanUtils.copyProperties(msg, vo);
            vo.setMine(Objects.equals(msg.getSenderId(), userId));
            return vo;
        }).collect(Collectors.toList());
        result.setRecords(records);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MsgDmMessageVo sendMessage(MsgDmSendDto dto) {
        Long userId = requireUserId();
        String content = dto.getContent() == null ? "" : dto.getContent().trim();
        if (!StringUtils.hasText(content)) {
            throw new BusinessException("消息内容不能为空");
        }
        if (content.length() > 2000) {
            throw new BusinessException("消息内容过长");
        }
        MsgDmMemberEntity self = requireMember(dto.getConversationId(), userId);
        UserEntity peer = userMapper.selectById(self.getPeerId());
        if (peer == null || peer.getStatus() == null || peer.getStatus() != 1) {
            throw new BusinessException("对方账号不可用");
        }
        MsgDmMemberEntity peerMember = requireMember(dto.getConversationId(), self.getPeerId());

        MsgDmMessageEntity message = new MsgDmMessageEntity();
        message.setConversationId(dto.getConversationId());
        message.setSenderId(userId);
        message.setContent(content);
        message.setDeleted(0);
        msgDmMessageMapper.insert(message);

        LocalDateTime now = message.getCreateTime() == null ? LocalDateTime.now() : message.getCreateTime();
        String preview = content.length() > 200 ? content.substring(0, 200) : content;

        self.setHidden(0);
        self.setLastMsgId(message.getId());
        self.setLastMsgPreview(preview);
        self.setLastMsgTime(now);
        self.setLastReadMsgId(message.getId());
        self.setUnreadCount(0);
        msgDmMemberMapper.updateById(self);

        msgDmMemberMapper.incrUnreadAndPreview(peerMember.getId(), message.getId(), preview, now);

        MsgDmConversationEntity conversation = msgDmConversationMapper.selectById(dto.getConversationId());
        if (conversation != null) {
            conversation.setLastMsgId(message.getId());
            conversation.setLastMsgTime(now);
            msgDmConversationMapper.updateById(conversation);
        }

        msgUnreadSupport.pushUnread(self.getPeerId());
        msgUnreadSupport.pushUnread(userId);

        MsgDmMessageVo vo = new MsgDmMessageVo();
        BeanUtils.copyProperties(message, vo);
        vo.setMine(true);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void hideConversation(Long conversationId) {
        Long userId = requireUserId();
        MsgDmMemberEntity self = requireMember(conversationId, userId);
        self.setHidden(1);
        msgDmMemberMapper.updateById(self);
        msgUnreadSupport.pushUnread(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pinConversation(Long conversationId, boolean pinned) {
        Long userId = requireUserId();
        MsgDmMemberEntity self = requireMember(conversationId, userId);
        self.setPinned(pinned ? 1 : 0);
        self.setPinnedTime(pinned ? LocalDateTime.now() : null);
        msgDmMemberMapper.updateById(self);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConversation(Long conversationId) {
        Long userId = requireUserId();
        MsgDmMemberEntity self = requireMember(conversationId, userId);
        Long maxMsgId = self.getLastMsgId();
        if (maxMsgId == null) {
            MsgDmMessageEntity latest = msgDmMessageMapper.selectOne(new LambdaQueryWrapper<MsgDmMessageEntity>()
                    .eq(MsgDmMessageEntity::getConversationId, conversationId)
                    .orderByDesc(MsgDmMessageEntity::getId)
                    .last("LIMIT 1"));
            maxMsgId = latest == null ? 0L : latest.getId();
        }
        self.setClearedBeforeId(maxMsgId);
        self.setHidden(1);
        self.setUnreadCount(0);
        self.setLastMsgId(null);
        self.setLastMsgPreview(null);
        self.setLastMsgTime(null);
        self.setLastReadMsgId(maxMsgId);
        msgDmMemberMapper.updateById(self);
        msgUnreadSupport.pushUnread(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllRead() {
        Long userId = requireUserId();
        msgDmMemberMapper.markAllReadByUserId(userId);
        msgUnreadSupport.pushUnread(userId);
    }

    @Override
    public Page<MsgDmUserVo> searchUsers(MsgDmUserSearchDto dto) {
        Long userId = requireUserId();
        int pageNum = dto.getPageNum() == null || dto.getPageNum() < 1 ? 1 : dto.getPageNum();
        int pageSize = dto.getPageSize() == null || dto.getPageSize() < 1 ? 30 : Math.min(dto.getPageSize(), 50);
        String keyword = StringUtils.hasText(dto.getKeyword()) ? dto.getKeyword().trim() : null;
        return userMapper.searchMsgDmUsers(new Page<>(pageNum, pageSize), keyword, userId);
    }

    private MsgDmConversationEntity getOrCreateConversation(Long userA, Long userB) {
        long low = Math.min(userA, userB);
        long high = Math.max(userA, userB);
        MsgDmConversationEntity existing = msgDmConversationMapper.selectOne(new LambdaQueryWrapper<MsgDmConversationEntity>()
                .eq(MsgDmConversationEntity::getUserLowId, low)
                .eq(MsgDmConversationEntity::getUserHighId, high)
                .last("LIMIT 1"));
        if (existing != null) {
            return existing;
        }
        MsgDmConversationEntity conversation = new MsgDmConversationEntity();
        conversation.setUserLowId(low);
        conversation.setUserHighId(high);
        conversation.setDeleted(0);
        try {
            msgDmConversationMapper.insert(conversation);
            return conversation;
        } catch (DuplicateKeyException ex) {
            MsgDmConversationEntity again = msgDmConversationMapper.selectOne(new LambdaQueryWrapper<MsgDmConversationEntity>()
                    .eq(MsgDmConversationEntity::getUserLowId, low)
                    .eq(MsgDmConversationEntity::getUserHighId, high)
                    .last("LIMIT 1"));
            if (again != null) {
                return again;
            }
            throw ex;
        }
    }

    /**
     * @param initialHidden 新建成员时的 hidden；已存在记录不覆盖
     */
    private MsgDmMemberEntity getOrCreateMember(Long conversationId, Long userId, Long peerId, int initialHidden) {
        MsgDmMemberEntity existing = msgDmMemberMapper.selectOne(new LambdaQueryWrapper<MsgDmMemberEntity>()
                .eq(MsgDmMemberEntity::getConversationId, conversationId)
                .eq(MsgDmMemberEntity::getUserId, userId)
                .last("LIMIT 1"));
        if (existing != null) {
            return existing;
        }
        MsgDmMemberEntity member = new MsgDmMemberEntity();
        member.setConversationId(conversationId);
        member.setUserId(userId);
        member.setPeerId(peerId);
        member.setHidden(initialHidden);
        member.setPinned(0);
        member.setUnreadCount(0);
        member.setLastReadMsgId(0L);
        member.setClearedBeforeId(0L);
        member.setDeleted(0);
        try {
            msgDmMemberMapper.insert(member);
            return member;
        } catch (DuplicateKeyException ex) {
            MsgDmMemberEntity again = msgDmMemberMapper.selectOne(new LambdaQueryWrapper<MsgDmMemberEntity>()
                    .eq(MsgDmMemberEntity::getConversationId, conversationId)
                    .eq(MsgDmMemberEntity::getUserId, userId)
                    .last("LIMIT 1"));
            if (again != null) {
                return again;
            }
            throw ex;
        }
    }

    private MsgDmMemberEntity requireMember(Long conversationId, Long userId) {
        MsgDmMemberEntity member = msgDmMemberMapper.selectOne(new LambdaQueryWrapper<MsgDmMemberEntity>()
                .eq(MsgDmMemberEntity::getConversationId, conversationId)
                .eq(MsgDmMemberEntity::getUserId, userId)
                .last("LIMIT 1"));
        if (member == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "会话不存在");
        }
        return member;
    }

    private MsgDmConversationVo toConversationVo(MsgDmMemberEntity member, UserEntity peer, Map<Long, String> avatarUrlCache) {
        MsgDmConversationVo vo = new MsgDmConversationVo();
        vo.setId(member.getConversationId());
        vo.setPeerUserId(member.getPeerId());
        vo.setLastMsgPreview(member.getLastMsgPreview());
        vo.setLastMsgTime(member.getLastMsgTime());
        vo.setUnreadCount(member.getUnreadCount() == null ? 0 : member.getUnreadCount());
        vo.setPinned(member.getPinned() == null ? 0 : member.getPinned());
        if (peer != null) {
            vo.setPeerUsername(peer.getUsername());
            vo.setPeerNickname(StringUtils.hasText(peer.getNickname()) ? peer.getNickname() : peer.getUsername());
            vo.setPeerAvatarId(peer.getAvatarId());
            vo.setPeerAvailable(peer.getStatus() != null && peer.getStatus() == 1);
            if (peer.getAvatarId() != null) {
                vo.setPeerAvatarUrl(avatarUrlCache.get(peer.getAvatarId()));
            }
        } else {
            vo.setPeerNickname("未知用户");
            vo.setPeerAvailable(false);
        }
        return vo;
    }

    private Map<Long, String> resolveAvatarUrls(Set<Long> avatarIds) {
        Map<Long, String> cache = new HashMap<>();
        if (avatarIds == null || avatarIds.isEmpty()) {
            return cache;
        }
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (Long avatarId : avatarIds) {
            futures.add(CompletableFuture.runAsync(() -> {
                String url = null;
                try {
                    Response<AttachmentVo> resp = attachmentFeign.getAttachmentById(avatarId);
                    if (resp != null && Objects.equals(resp.getCode(), Response.SUCCESS_CODE) && resp.getData() != null) {
                        url = resp.getData().getFileUrl();
                    }
                } catch (Exception e) {
                    log.warn("解析私信对方头像失败 avatarId={}: {}", avatarId, e.getMessage());
                }
                synchronized (cache) {
                    cache.put(avatarId, url);
                }
            }, AVATAR_RESOLVE_EXECUTOR));
        }
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        return cache;
    }

    private Long requireUserId() {
        Long userId = UserContextUtils.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.LOGIN_EXPIRED, "未登录");
        }
        return userId;
    }
}
