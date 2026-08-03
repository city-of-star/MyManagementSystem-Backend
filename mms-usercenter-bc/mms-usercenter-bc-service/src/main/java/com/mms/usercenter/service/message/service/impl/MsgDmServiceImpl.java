package com.mms.usercenter.service.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.webmvc.utils.UserContextUtils;
import com.mms.usercenter.common.auth.entity.UserEntity;
import com.mms.usercenter.common.message.dto.MsgDmConversationPageQueryDto;
import com.mms.usercenter.common.message.dto.MsgDmMessagePageQueryDto;
import com.mms.usercenter.common.message.dto.MsgDmOpenDto;
import com.mms.usercenter.common.message.dto.MsgDmSendDto;
import com.mms.usercenter.common.message.entity.MsgDmConversationEntity;
import com.mms.usercenter.common.message.entity.MsgDmMemberEntity;
import com.mms.usercenter.common.message.entity.MsgDmMessageEntity;
import com.mms.usercenter.common.message.vo.MsgDmConversationVo;
import com.mms.usercenter.common.message.vo.MsgDmMessageVo;
import com.mms.usercenter.service.auth.mapper.UserMapper;
import com.mms.usercenter.service.message.mapper.MsgDmConversationMapper;
import com.mms.usercenter.service.message.mapper.MsgDmMemberMapper;
import com.mms.usercenter.service.message.mapper.MsgDmMessageMapper;
import com.mms.usercenter.service.message.service.MsgDmService;
import com.mms.usercenter.service.message.support.MsgUnreadSupport;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
@Service
public class MsgDmServiceImpl implements MsgDmService {

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

    @Override
    public Page<MsgDmConversationVo> getConversationPage(MsgDmConversationPageQueryDto dto) {
        Long userId = requireUserId();
        int pageNum = dto.getPageNum() == null || dto.getPageNum() < 1 ? 1 : dto.getPageNum();
        int pageSize = dto.getPageSize() == null || dto.getPageSize() < 1 ? 20 : dto.getPageSize();

        List<MsgDmMemberEntity> members = msgDmMemberMapper.selectList(new LambdaQueryWrapper<MsgDmMemberEntity>()
                .eq(MsgDmMemberEntity::getUserId, userId)
                .eq(MsgDmMemberEntity::getHidden, 0)
                .isNotNull(MsgDmMemberEntity::getLastMsgId));

        Set<Long> peerIds = members.stream().map(MsgDmMemberEntity::getPeerId).collect(Collectors.toSet());
        Map<Long, UserEntity> peerMap = peerIds.isEmpty() ? Map.of()
                : userMapper.selectBatchIds(peerIds).stream().collect(Collectors.toMap(UserEntity::getId, u -> u, (a, b) -> a));

        List<MsgDmConversationVo> all = new ArrayList<>();
        for (MsgDmMemberEntity member : members) {
            if (member.getLastMsgId() == null && (member.getClearedBeforeId() == null || member.getClearedBeforeId() == 0)) {
                continue;
            }
            UserEntity peer = peerMap.get(member.getPeerId());
            if (StringUtils.hasText(dto.getKeyword())) {
                if (peer == null) {
                    continue;
                }
                String kw = dto.getKeyword();
                boolean hit = (peer.getNickname() != null && peer.getNickname().contains(kw))
                        || (peer.getUsername() != null && peer.getUsername().contains(kw))
                        || (peer.getRealName() != null && peer.getRealName().contains(kw));
                if (!hit) {
                    continue;
                }
            }
            all.add(toConversationVo(member, peer));
        }

        all.sort(Comparator
                .comparing((MsgDmConversationVo v) -> v.getPinned() != null && v.getPinned() == 1 ? 0 : 1)
                .thenComparing(v -> v.getUnreadCount() != null && v.getUnreadCount() > 0 ? 0 : 1)
                .thenComparing(MsgDmConversationVo::getLastMsgTime, Comparator.nullsLast(Comparator.reverseOrder())));

        int from = Math.min((pageNum - 1) * pageSize, all.size());
        int to = Math.min(from + pageSize, all.size());
        Page<MsgDmConversationVo> page = new Page<>(pageNum, pageSize, all.size());
        page.setRecords(all.subList(from, to));
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
        MsgDmMemberEntity self = getOrCreateMember(conversation.getId(), userId, peerId);
        getOrCreateMember(conversation.getId(), peerId, userId);
        if (self.getHidden() != null && self.getHidden() == 1) {
            self.setHidden(0);
            msgDmMemberMapper.updateById(self);
        }
        return toConversationVo(self, peer);
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
        int pageSize = dto.getPageSize() == null || dto.getPageSize() < 1 ? 50 : dto.getPageSize();
        long clearedBefore = self.getClearedBeforeId() == null ? 0L : self.getClearedBeforeId();

        Page<MsgDmMessageEntity> page = msgDmMessageMapper.selectPage(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<MsgDmMessageEntity>()
                        .eq(MsgDmMessageEntity::getConversationId, dto.getConversationId())
                        .gt(MsgDmMessageEntity::getId, clearedBefore)
                        .orderByDesc(MsgDmMessageEntity::getId));

        Long maxId = page.getRecords().stream().map(MsgDmMessageEntity::getId).max(Long::compareTo).orElse(null);
        if (maxId != null && (self.getLastReadMsgId() == null || maxId > self.getLastReadMsgId())) {
            self.setLastReadMsgId(maxId);
            self.setUnreadCount(0);
            if (self.getHidden() != null && self.getHidden() == 1) {
                self.setHidden(0);
            }
            msgDmMemberMapper.updateById(self);
            msgUnreadSupport.pushUnread(userId);
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

        peerMember.setHidden(0);
        peerMember.setLastMsgId(message.getId());
        peerMember.setLastMsgPreview(preview);
        peerMember.setLastMsgTime(now);
        int unread = peerMember.getUnreadCount() == null ? 0 : peerMember.getUnreadCount();
        peerMember.setUnreadCount(unread + 1);
        msgDmMemberMapper.updateById(peerMember);

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
        msgDmConversationMapper.insert(conversation);
        return conversation;
    }

    private MsgDmMemberEntity getOrCreateMember(Long conversationId, Long userId, Long peerId) {
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
        member.setHidden(0);
        member.setPinned(0);
        member.setUnreadCount(0);
        member.setLastReadMsgId(0L);
        member.setClearedBeforeId(0L);
        member.setDeleted(0);
        msgDmMemberMapper.insert(member);
        return member;
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

    private MsgDmConversationVo toConversationVo(MsgDmMemberEntity member, UserEntity peer) {
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
        } else {
            vo.setPeerNickname("未知用户");
            vo.setPeerAvailable(false);
        }
        return vo;
    }

    private Long requireUserId() {
        Long userId = UserContextUtils.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.LOGIN_EXPIRED, "未登录");
        }
        return userId;
    }
}
