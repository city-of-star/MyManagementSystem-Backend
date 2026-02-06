package com.mms.base.service.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.system.entity.AttachmentEntity;
import com.mms.base.common.system.dto.*;
import com.mms.base.common.system.vo.AttachmentVo;
import com.mms.base.common.system.properties.AttachmentProperties;
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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private AttachmentProperties attachmentProperties;

    @Override
    public Page<AttachmentVo> getAttachmentPage(AttachmentPageQueryDto dto) {
        try {
            log.info("分页查询附件列表，参数：{}", dto);
            Page<AttachmentEntity> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            Page<AttachmentEntity> entityPage = attachmentMapper.getAttachmentPage(page, dto);
            Page<AttachmentVo> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
            voPage.setRecords(entityPage.getRecords().stream().map(this::convertToVo).collect(Collectors.toList()));
            return voPage;
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

    private AttachmentVo convertToVo(AttachmentEntity entity) {
        if (entity == null) {
            return null;
        }
        AttachmentVo vo = new AttachmentVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AttachmentVo uploadAttachment(MultipartFile file, String businessType, Long businessId, String remark) {
        try {
            if (file == null || file.isEmpty()) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "上传文件不能为空");
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                originalFilename = "";
            }
            // 防止路径穿越，仅取文件名部分
            originalFilename = Paths.get(originalFilename).getFileName().toString();

            String fileType = extractExtensionLower(originalFilename);
            String mimeType = file.getContentType();
            long fileSize = file.getSize();

            String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            validateUpload(fileType, mimeType, fileSize);
            String storedFileName = buildStoredFileName(originalFilename, fileType);

            Path baseDir = Paths.get(attachmentProperties.getStoragePath()).toAbsolutePath().normalize();
            Path targetDir = baseDir.resolve(dateDir);
            Files.createDirectories(targetDir);
            Path targetFile = targetDir.resolve(storedFileName);

            try (InputStream in = file.getInputStream()) {
                Files.copy(in, targetFile, StandardCopyOption.REPLACE_EXISTING);
            }

            String urlPrefix = normalizeUrlPrefix(attachmentProperties.getUrlPrefix());
            String fileUrl = urlPrefix + "/" + dateDir + "/" + storedFileName;

            AttachmentEntity entity = new AttachmentEntity();
            entity.setFileName(storedFileName);
            entity.setOriginalName(originalFilename);
            // file_path 存相对目录，便于跨环境迁移
            entity.setFilePath(dateDir + "/");
            entity.setFileUrl(fileUrl);
            entity.setFileSize(fileSize);
            entity.setFileType(StringUtils.hasText(fileType) ? fileType : "bin");
            entity.setMimeType(mimeType);
            entity.setStorageType("local");
            entity.setBusinessType(businessType);
            entity.setBusinessId(businessId);
            entity.setStatus(1);
            entity.setRemark(remark);
            entity.setDeleted(0);
            attachmentMapper.insert(entity);

            return convertToVo(entity);
        } catch (BusinessException e) {
            throw e;
        } catch (IOException e) {
            log.error("上传附件写入文件失败：{}", e.getMessage(), e);
            throw new ServerException("上传附件失败（写入文件失败）", e);
        } catch (Exception e) {
            log.error("上传附件失败：{}", e.getMessage(), e);
            throw new ServerException("上传附件失败", e);
        }
    }

    private String extractExtensionLower(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "";
        }
        int lastDot = filename.lastIndexOf('.');
        if (lastDot < 0 || lastDot == filename.length() - 1) {
            return "";
        }
        String ext = filename.substring(lastDot + 1).trim();
        if (!StringUtils.hasText(ext)) {
            return "";
        }
        // 简单兜底：避免超长/异常扩展名
        if (ext.length() > 20) {
            return "";
        }
        return ext.toLowerCase();
    }

    private void validateUpload(String fileType, String mimeType, long fileSize) {
        Integer maxSizeMb = attachmentProperties.getMaxSize();
        if (maxSizeMb != null && maxSizeMb > 0) {
            long maxBytes = (long) maxSizeMb * 1024 * 1024;
            if (fileSize > maxBytes) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "文件大小超过限制：" + maxSizeMb + "MB");
            }
        }

        if (attachmentProperties.getAllowedTypes() != null && !attachmentProperties.getAllowedTypes().isEmpty()) {
            if (!StringUtils.hasText(fileType)) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "文件扩展名不能为空");
            }
            boolean allowed = attachmentProperties.getAllowedTypes().stream()
                    .filter(StringUtils::hasText)
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .anyMatch(t -> t.equals(fileType));
            if (!allowed) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "不支持的文件类型：" + fileType);
            }
        }

        Boolean enableTypeValidation = attachmentProperties.getEnableTypeValidation();
        if (Boolean.TRUE.equals(enableTypeValidation) && StringUtils.hasText(mimeType) && StringUtils.hasText(fileType)) {
            String mt = mimeType.toLowerCase();
            // 轻量校验：图片和 PDF 最容易伪造扩展名，这里做一下兜底
            if (isImageExt(fileType) && !mt.startsWith("image/")) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "文件类型校验失败：扩展名与MIME类型不匹配");
            }
            if ("pdf".equals(fileType) && !mt.contains("pdf")) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "文件类型校验失败：扩展名与MIME类型不匹配");
            }
        }
    }

    private boolean isImageExt(String ext) {
        return "jpg".equals(ext) || "jpeg".equals(ext) || "png".equals(ext) || "gif".equals(ext)
                || "bmp".equals(ext) || "webp".equals(ext);
    }

    private String buildStoredFileName(String originalFilename, String fileType) {
        String strategy = attachmentProperties.getNamingStrategy();
        if (!StringUtils.hasText(strategy)) {
            strategy = "uuid";
        }
        strategy = strategy.trim().toLowerCase();

        String suffix = StringUtils.hasText(fileType) ? "." + fileType : "";
        return switch (strategy) {
            case "timestamp" -> System.currentTimeMillis() + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8) + suffix;
            case "original" -> {
                String safe = sanitizeFileName(originalFilename);
                if (!StringUtils.hasText(safe)) {
                    yield UUID.randomUUID().toString().replace("-", "") + suffix;
                }
                // 保留原始扩展名/文件名（若没扩展名则追加 fileType）
                if (!safe.contains(".") && StringUtils.hasText(fileType)) {
                    safe = safe + suffix;
                }
                yield safe;
            }
            default -> UUID.randomUUID().toString().replace("-", "") + suffix;
        };
    }

    private String sanitizeFileName(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "";
        }
        // Windows 不允许的字符：\ / : * ? " < > |
        String f = filename.trim()
                .replace("\\", "_")
                .replace("/", "_")
                .replace(":", "_")
                .replace("*", "_")
                .replace("?", "_")
                .replace("\"", "_")
                .replace("<", "_")
                .replace(">", "_")
                .replace("|", "_");
        // 去掉控制字符
        f = f.replaceAll("[\\r\\n\\t]", "_");
        // 限制长度，避免极端情况
        if (f.length() > 200) {
            f = f.substring(0, 200);
        }
        return f;
    }

    private String normalizeUrlPrefix(String urlPrefix) {
        String p = StringUtils.hasText(urlPrefix) ? urlPrefix.trim() : "";
        if (!p.startsWith("/")) {
            p = "/" + p;
        }
        if (p.endsWith("/")) {
            p = p.substring(0, p.length() - 1);
        }
        return p;
    }
}