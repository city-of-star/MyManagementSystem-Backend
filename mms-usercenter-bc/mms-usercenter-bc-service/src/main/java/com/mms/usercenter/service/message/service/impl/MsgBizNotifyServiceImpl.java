package com.mms.usercenter.service.message.service.impl;

import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import com.mms.usercenter.common.auth.entity.UserEntity;
import com.mms.usercenter.common.message.dto.MsgBizNotifyDto;
import com.mms.usercenter.common.message.entity.MsgSysInboxEntity;
import com.mms.usercenter.common.message.vo.MsgBizNotifyVo;
import com.mms.usercenter.service.auth.mapper.UserMapper;
import com.mms.usercenter.service.message.mapper.MsgSysInboxMapper;
import com.mms.usercenter.service.message.service.MsgBizNotifyService;
import com.mms.usercenter.service.message.support.MsgUnreadSupport;
import com.mms.usercenter.service.message.utils.MsgLinkPathUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 实现功能【业务系统通知服务实现】
 * <p>
 * 直接写 msg_sys_inbox，不写公告发件表；按 bizType+bizId+userId 幂等。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-06
 */
@Slf4j
@Service
public class MsgBizNotifyServiceImpl implements MsgBizNotifyService {

    @Resource
    private MsgSysInboxMapper msgSysInboxMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private MsgUnreadSupport msgUnreadSupport;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MsgBizNotifyVo notify(MsgBizNotifyDto dto) {
        try {
            if (dto == null || dto.getUserId() == null
                    || !StringUtils.hasText(dto.getBizType())
                    || !StringUtils.hasText(dto.getBizId())
                    || !StringUtils.hasText(dto.getTitle())
                    || !StringUtils.hasText(dto.getContentText())) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "业务通知参数不完整");
            }
            String bizType = dto.getBizType().trim();
            String bizId = dto.getBizId().trim();
            String title = dto.getTitle().trim();
            String contentText = dto.getContentText().trim();
            Long userId = dto.getUserId();

            UserEntity user = userMapper.selectById(userId);
            if (user == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "收件用户不存在");
            }
            if (!Integer.valueOf(1).equals(user.getStatus())) {
                log.info("收件用户已禁用，跳过业务通知，userId={}，bizType={}，bizId={}", userId, bizType, bizId);
                return skipResult(null);
            }

            MsgSysInboxEntity existing = msgSysInboxMapper.selectByBizUserIncludeDeleted(bizType, bizId, userId);
            if (existing != null) {
                log.info("业务通知已存在，幂等跳过，inboxId={}，userId={}，bizType={}，bizId={}，deleted={}",
                        existing.getId(), userId, bizType, bizId, existing.getDeleted());
                return skipResult(existing.getId());
            }

            MsgSysInboxEntity inbox = new MsgSysInboxEntity();
            inbox.setUserId(userId);
            inbox.setAnnounceId(null);
            inbox.setBizType(bizType);
            inbox.setBizId(bizId);
            inbox.setTitle(title);
            inbox.setContentHtml(null);
            inbox.setContentText(contentText);
            inbox.setLinkPath(MsgLinkPathUtils.normalizeOptional(dto.getLinkPath()));
            inbox.setStarred(0);
            inbox.setReadFlag(0);
            inbox.setDeleted(0);
            try {
                msgSysInboxMapper.insert(inbox);
            } catch (Exception ex) {
                MsgSysInboxEntity again = msgSysInboxMapper.selectByBizUserIncludeDeleted(bizType, bizId, userId);
                if (again != null) {
                    log.info("业务通知并发写入命中幂等，inboxId={}，userId={}，bizType={}，bizId={}",
                            again.getId(), userId, bizType, bizId);
                    return skipResult(again.getId());
                }
                throw ex;
            }

            msgUnreadSupport.pushUnread(userId);
            log.info("业务通知投递成功，inboxId={}，userId={}，bizType={}，bizId={}",
                    inbox.getId(), userId, bizType, bizId);
            MsgBizNotifyVo vo = new MsgBizNotifyVo();
            vo.setInboxId(inbox.getId());
            vo.setCreated(true);
            return vo;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("业务通知投递失败：{}", e.getMessage(), e);
            throw new ServerException("业务通知投递失败", e);
        }
    }

    private MsgBizNotifyVo skipResult(Long inboxId) {
        MsgBizNotifyVo vo = new MsgBizNotifyVo();
        vo.setInboxId(inboxId);
        vo.setCreated(false);
        return vo;
    }
}
