package com.mms.base.service.file.service.impl;

import com.mms.base.common.file.properties.FileProperties;
import com.mms.base.common.file.vo.FileVo;
import com.mms.base.service.file.service.FileService;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import com.mms.common.core.utils.DateUtils;
import com.mms.common.core.utils.IdUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 实现功能【文件服务实现类】
 * <p>
 * 负责文件的本地存储实现
 *
 * @author li.hongyu
 * @date 2026-02-09 15:26:06
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Resource
    private FileProperties fileProperties;

    private Path baseDir;

    @PostConstruct
    public void init() {
        String storagePath = fileProperties.getStoragePath();
        String urlPrefix = fileProperties.getUrlPrefix();
        if (!StringUtils.hasText(storagePath) || !StringUtils.hasText(urlPrefix)) {
            throw new ServerException("文件存储配置异常，请检查 file.upload.storagePath / file.upload.urlPrefix");
        }

        // 将相对存储路径转换为绝对路径并缓存
        this.baseDir = Paths.get(storagePath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(baseDir);
        } catch (IOException e) {
            log.error("文件存储路径不可用: {}", baseDir, e);
            throw new ServerException("文件存储路径不可用: " + baseDir, e);
        }
    }

    @Override
    public FileVo store(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "上传文件不能为空");
            }

            // 获取原始文件名
            String originalFileName = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
            // 做路径穿越防护
            originalFileName = Paths.get(originalFileName).getFileName().toString();
            // 获取文件后缀
            String fileType = extractExtensionLower(originalFileName);
            // 获取文件 mime 类型
            String mimeType = file.getContentType();
            // 获取文件大小
            long fileSize = file.getSize();
            // 校验文件的大小与类型
            validateUpload(fileType, fileSize);
            // 生成日期目录
            String dateDir = DateUtils.todayDir();
            // 生成存储文件名
            String storedFileName = IdUtils.timestampId() + '.' + fileType;
            // 生成目录路径（基础路径+日期路径）
            Path targetDir = baseDir.resolve(dateDir);
            // 递归创建目标目录及其所有父目录
            Files.createDirectories(targetDir);
            // 拼接成完整的文件路径
            Path targetFile = targetDir.resolve(storedFileName);
            // 写入文件（如果存在就覆盖）
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, targetFile, StandardCopyOption.REPLACE_EXISTING);
            }
            // 生成访问 URL
            String fileUrl = fileProperties.getUrlPrefix() + "/" + dateDir + "/" + storedFileName;
            // 组装文件信息并返回
            FileVo fileVo = new FileVo();
            fileVo.setOriginalFileName(originalFileName);
            fileVo.setStoredFileName(storedFileName);
            fileVo.setRelativePath(dateDir + "/");
            fileVo.setFileUrl(fileUrl);
            fileVo.setSize(fileSize);
            fileVo.setFileType(StringUtils.hasText(fileType) ? fileType : "bin");
            fileVo.setMimeType(mimeType);
            return fileVo;
        } catch (BusinessException e) {
            throw e;
        } catch (IOException e) {
            log.error("文件写入失败：{}", e.getMessage(), e);
            throw new ServerException("文件存储失败（写入异常）", e);
        } catch (Exception e) {
            log.error("文件存储失败：{}", e.getMessage(), e);
            throw new ServerException("文件存储失败", e);
        }
    }

    @Override
    public InputStream openStream(String relativePath) throws IOException {
        Path filePath = resolveFilePath(relativePath);
        return Files.newInputStream(filePath);
    }

    @Override
    public long getFileSize(String relativePath) throws IOException {
        Path filePath = resolveFilePath(relativePath);
        return Files.size(filePath);
    }

    @Override
    public String getContentType(String relativePath) throws IOException {
        Path filePath = resolveFilePath(relativePath);
        return Files.probeContentType(filePath);
    }

    @Override
    public boolean exists(String relativePath) {
        try {
            Path filePath = resolveFilePath(relativePath);
            return Files.exists(filePath) && Files.isRegularFile(filePath);
        } catch (Exception e) {
            log.warn("检查文件是否存在失败，相对路径：{}，原因：{}", relativePath, e.getMessage());
            return false;
        }
    }

    /**
     * 将相对路径解析为物理文件路径，并做路径穿越防护
     */
    private Path resolveFilePath(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "附件路径不能为空");
        }
        Path filePath = baseDir.resolve(relativePath).normalize();
        if (!filePath.startsWith(baseDir)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "非法的附件访问路径");
        }
        return filePath;
    }

    /**
     * 提取文件名中的扩展名，无扩展名或异常情况返回空串
     */
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
        if (ext.length() > 20) {
            return "";
        }
        return ext;
    }

    /**
     * 校验上传文件的大小与类型
     */
    private void validateUpload(String fileType, long fileSize) {

        // 校验文件大小
        Integer maxSizeMb = fileProperties.getMaxSize();
        if (maxSizeMb != null && maxSizeMb > 0) {
            long maxBytes = (long) maxSizeMb * 1024 * 1024;
            if (fileSize > maxBytes) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "文件大小超过限制：" + maxSizeMb + "MB");
            }
        }

        // 校验文件类型
        if (fileProperties.getAllowedTypes() != null && !fileProperties.getAllowedTypes().isEmpty()) {
            if (!StringUtils.hasText(fileType)) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "文件扩展名不能为空");
            }
            boolean allowed = fileProperties.getAllowedTypes().stream()
                    .filter(StringUtils::hasText)
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .anyMatch(t -> t.equals(fileType));
            if (!allowed) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "不支持的文件类型：" + fileType);
            }
        }
    }
}