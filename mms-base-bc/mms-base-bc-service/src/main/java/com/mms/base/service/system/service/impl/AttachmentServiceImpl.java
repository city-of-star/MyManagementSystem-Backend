package com.mms.base.service.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.system.entity.AttachmentEntity;
import com.mms.base.common.system.dto.*;
import com.mms.base.common.system.vo.AttachmentVo;
import com.mms.base.service.file.service.FileService;
import com.mms.base.common.file.vo.FileVo;
import com.mms.base.service.system.mapper.AttachmentMapper;
import com.mms.base.service.system.service.AttachmentService;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 实现功能【附件服务实现类】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-06 14:36:38
 */
@Slf4j
@Service
public class AttachmentServiceImpl implements AttachmentService {

    @Resource
    private AttachmentMapper attachmentMapper;

    @Resource
    private FileService fileService;

    @Override
    public Page<AttachmentVo> getAttachmentPage(AttachmentPageQueryDto dto) {
        try {
            log.info("分页查询附件列表，参数：{}", dto);
            Page<AttachmentVo> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            return attachmentMapper.getAttachmentPage(page, dto);
        } catch (Exception e) {
            log.error("分页查询附件列表失败：{}", e.getMessage(), e);
            throw new ServerException("查询附件列表失败", e);
        }
    }

    @Override
    public AttachmentVo getAttachmentById(Long attachmentId) {
        try {
            log.info("根据ID查询附件，attachmentId：{}", attachmentId);
            if (attachmentId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "附件ID不能为空");
            }
            AttachmentEntity attachment = attachmentMapper.selectById(attachmentId);
            if (attachment == null || Objects.equals(attachment.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "附件不存在");
            }
            return convertToVo(attachment);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("根据ID查询附件失败：{}", e.getMessage(), e);
            throw new ServerException("查询附件失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AttachmentVo createAttachment(AttachmentCreateDto dto) {
        try {
            log.info("创建附件记录，参数：{}", dto);
            AttachmentEntity entity = new AttachmentEntity();
            entity.setFileName(dto.getFileName());
            entity.setOriginalName(dto.getOriginalName());
            entity.setFilePath(dto.getFilePath());
            entity.setFileUrl(dto.getFileUrl());
            entity.setFileSize(dto.getFileSize());
            entity.setFileType(dto.getFileType());
            entity.setMimeType(dto.getMimeType());
            entity.setStorageType(StringUtils.hasText(dto.getStorageType()) ? dto.getStorageType() : "local");
            entity.setBusinessType(dto.getBusinessType());
            entity.setBusinessId(dto.getBusinessId());
            entity.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
            entity.setRemark(dto.getRemark());
            entity.setDeleted(0);
            attachmentMapper.insert(entity);
            return convertToVo(entity);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建附件记录失败：{}", e.getMessage(), e);
            throw new ServerException("创建附件失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AttachmentVo updateAttachment(AttachmentUpdateDto dto) {
        try {
            log.info("更新附件信息，参数：{}", dto);
            AttachmentEntity attachment = attachmentMapper.selectById(dto.getId());
            if (attachment == null || Objects.equals(attachment.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "附件不存在");
            }
            if (StringUtils.hasText(dto.getBusinessType())) {
                attachment.setBusinessType(dto.getBusinessType());
            }
            if (dto.getBusinessId() != null) {
                attachment.setBusinessId(dto.getBusinessId());
            }
            if (StringUtils.hasText(dto.getRemark())) {
                attachment.setRemark(dto.getRemark());
            }
            attachmentMapper.updateById(attachment);
            return convertToVo(attachment);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新附件信息失败：{}", e.getMessage(), e);
            throw new ServerException("更新附件失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAttachment(Long attachmentId) {
        try {
            log.info("删除附件，attachmentId：{}", attachmentId);
            if (attachmentId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "附件ID不能为空");
            }
            AttachmentEntity attachment = attachmentMapper.selectById(attachmentId);
            if (attachment == null || Objects.equals(attachment.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "附件不存在");
            }
            attachmentMapper.deleteById(attachmentId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除附件失败：{}", e.getMessage(), e);
            throw new ServerException("删除附件失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void hardDeleteAttachment(Long attachmentId) {
        try {
            log.info("硬删除附件（删除物理文件 + 物理删除记录），attachmentId：{}", attachmentId);
            if (attachmentId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "附件ID不能为空");
            }
            AttachmentEntity attachment = attachmentMapper.selectById(attachmentId);
            if (attachment == null || Objects.equals(attachment.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "附件不存在或已删除");
            }
            // 组装相对文件路径
            Path relativePath = Paths.get(attachment.getFilePath()).resolve(attachment.getFileName());
            // 删除物理文件
            fileService.deleteIfExists(relativePath.toString());
            // 物理删除数据库记录
            attachmentMapper.hardDeleteById(attachmentId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("硬删除附件失败：{}", e.getMessage(), e);
            throw new ServerException("硬删除附件失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteAttachment(AttachmentBatchDeleteDto dto) {
        try {
            log.info("批量删除附件，ids：{}", dto.getIds());
            if (dto.getIds() == null || dto.getIds().isEmpty()) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "附件ID列表不能为空");
            }
            for (Long id : dto.getIds()) {
                deleteAttachment(id);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量删除附件失败：{}", e.getMessage(), e);
            throw new ServerException("批量删除附件失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void switchAttachmentStatus(AttachmentStatusSwitchDto dto) {
        try {
            log.info("切换附件状态，id：{}，status：{}", dto.getId(), dto.getStatus());
            AttachmentEntity attachment = attachmentMapper.selectById(dto.getId());
            if (attachment == null || Objects.equals(attachment.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "附件不存在");
            }
            if (dto.getStatus() != 0 && dto.getStatus() != 1) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "状态值只能是0或1");
            }
            attachment.setStatus(dto.getStatus());
            attachment.setUpdateTime(LocalDateTime.now());
            attachmentMapper.updateById(attachment);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("切换附件状态失败：{}", e.getMessage(), e);
            throw new ServerException("切换附件状态失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AttachmentVo uploadAttachment(MultipartFile file, String businessType, Long businessId, String remark) {
        try {
            // 空值校验
            if (file == null || file.isEmpty()) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "上传文件不能为空");
            }
            // 调用文件服务完成物理存储，获取存储结果
            FileVo fileVo = fileService.store(file);
            // 组装附件实体并入库
            AttachmentEntity entity = new AttachmentEntity();
            entity.setFileName(fileVo.getStoredFileName());
            entity.setOriginalName(fileVo.getOriginalFileName());
            entity.setFilePath(fileVo.getRelativePath());
            entity.setFileUrl(fileVo.getFileUrl());
            entity.setFileSize(fileVo.getSize());
            entity.setFileType(fileVo.getFileType());
            entity.setMimeType(fileVo.getMimeType());
            entity.setStorageType("local");
            entity.setBusinessType(businessType);
            entity.setBusinessId(businessId);
            entity.setStatus(1);
            entity.setRemark(remark);
            entity.setDeleted(0);
            attachmentMapper.insert(entity);
            // 转换为 VO 返回
            return convertToVo(entity);
        } catch (BusinessException e) {
            throw e;
        } catch (IOException e) {
            log.error("上传附件失败（文件服务 IO 异常）：{}", e.getMessage(), e);
            throw new ServerException("上传附件失败（文件写入失败）", e);
        } catch (Exception e) {
            log.error("上传附件失败：{}", e.getMessage(), e);
            throw new ServerException("上传附件失败", e);
        }
    }

    private AttachmentVo convertToVo(AttachmentEntity entity) {
        if (entity == null) {
            return null;
        }
        AttachmentVo vo = new AttachmentVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}