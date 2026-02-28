package com.mms.base.service.system.job;

import com.mms.base.common.system.entity.AttachmentEntity;
import com.mms.base.service.file.service.FileService;
import com.mms.base.service.system.job.dto.AttachmentCleanJobDto;
import com.mms.base.service.system.mapper.AttachmentMapper;
import com.mms.common.job.JobHandler;
import com.mms.common.job.utils.JobParamUtils;
import com.mms.job.common.annotation.JobDefinition;
import com.mms.job.common.enums.JobTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 实现功能【附件物理清理任务处理器】
 * <p>
 * 定期扫描已被逻辑删除的附件记录，删除物理文件并物理删除数据库记录。
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-25 11:08:20
 */
@Slf4j
@Component
@JobDefinition(type = JobTypeEnum.ATTACHMENT_CLEAN, paramClass = AttachmentCleanJobDto.class)
public class AttachmentCleanJobHandler implements JobHandler {

    @Resource
    private AttachmentMapper attachmentMapper;

    @Resource
    private FileService fileService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(String dtoJson) {
        // 解析参数
        AttachmentCleanJobDto dto = JobParamUtils.parseParams(dtoJson, AttachmentCleanJobDto.class);

        int a = 1 / 0;
        
        log.info("开始执行附件清理任务，参数：batchSize={}, deletedDays={}, deletePhysicalFile={}, storageType={}, businessType={}, fileType={}, maxFileSize={}, minFileSize={}, pathPattern={}, retryCount={}, continueOnError={}, orderBy={}",
                dto.getBatchSize(), dto.getDeletedDays(), dto.getDeletePhysicalFile(),
                dto.getStorageType(), dto.getBusinessType(), dto.getFileType(),
                dto.getMaxFileSize(), dto.getMinFileSize(), dto.getPathPattern(),
                dto.getRetryCount(), dto.getContinueOnError(), dto.getOrderBy());

        // 计算删除时间阈值（逻辑删除时间必须早于此时间才清理）
        LocalDateTime deleteBeforeTime = null;
        if (dto.getDeletedDays() != null && dto.getDeletedDays() > 0) {
            deleteBeforeTime = LocalDateTime.now().minusDays(dto.getDeletedDays());
        }

        // 批次大小（为null时使用默认100）
        int batchSize = dto.getBatchSize() != null && dto.getBatchSize() > 0 ? dto.getBatchSize() : 100;

        // 查询一批已逻辑删除的附件记录
        List<AttachmentEntity> records = attachmentMapper.selectDeletedForClean(
                batchSize,
                deleteBeforeTime,
                dto.getStorageType(),
                dto.getBusinessType(),
                dto.getOrderBy()
        );

        if (records == null || records.isEmpty()) {
            log.info("本次附件清理任务无待清理记录");
            return;
        }

        log.info("本次附件清理任务共需处理记录数：{}", records.size());
        
        // 编译路径匹配模式
        Pattern pathPattern = null;
        if (StringUtils.hasText(dto.getPathPattern())) {
            try {
                pathPattern = Pattern.compile(dto.getPathPattern());
            } catch (Exception e) {
                log.warn("路径匹配模式编译失败，将忽略此过滤条件，pathPattern={}，错误：{}", dto.getPathPattern(), e.getMessage());
            }
        }

        // 附件删除成功次数
        int successCount = 0;
        // 附件删除失败次数
        int failCount = 0;
        
        for (AttachmentEntity attachment : records) {
            try {
                // 应用过滤条件
                if (!shouldProcess(attachment, dto, pathPattern)) {
                    log.debug("附件记录不满足过滤条件，跳过处理，attachmentId={}", attachment.getId());
                    continue;
                }

                // 删除物理文件
                if (Boolean.TRUE.equals(dto.getDeletePhysicalFile())) {
                    if (!StringUtils.hasText(attachment.getFilePath()) || !StringUtils.hasText(attachment.getFileName())) {
                        log.warn("附件记录缺少 filePath/fileName，跳过物理文件删除，仅删除数据库记录，attachmentId={}", attachment.getId());
                    } else {
                        // 组装相对文件路径
                        Path relativePath = Paths.get(attachment.getFilePath()).resolve(attachment.getFileName());
                        // 删除物理文件（不存在则忽略），支持重试
                        deleteFileWithRetry(relativePath.toString(), dto.getRetryCount() != null ? dto.getRetryCount() : 0);
                    }
                }

                // 物理删除数据库记录
                attachmentMapper.hardDeleteById(attachment.getId());
                successCount++;
                log.debug("附件清理成功，attachmentId={}，path={}", attachment.getId(), 
                        attachment.getFilePath() != null && attachment.getFileName() != null 
                                ? Paths.get(attachment.getFilePath()).resolve(attachment.getFileName()) 
                                : "N/A");
            } catch (Exception e) {
                failCount++;
                if (Boolean.TRUE.equals(dto.getContinueOnError())) {
                    // 单条异常不影响其他记录的清理
                    log.error("附件清理失败，attachmentId={}，错误：{}", attachment.getId(), e.getMessage(), e);
                } else {
                    // 如果配置了遇到错误不继续，则抛出异常
                    log.error("附件清理失败且配置了不继续处理，attachmentId={}，错误：{}", attachment.getId(), e.getMessage(), e);
                    throw e;
                }
            }
        }
        
        log.info("附件清理任务执行完成，成功：{}，失败：{}", successCount, failCount);
    }

    /**
     * 判断是否应该处理该附件记录
     */
    private boolean shouldProcess(AttachmentEntity attachment, AttachmentCleanJobDto dto, Pattern pathPattern) {
        // 文件类型过滤
        List<String> fileTypeList = dto.getFileType();
        if (fileTypeList != null && !fileTypeList.isEmpty()) {
            String attachmentFileType = attachment.getFileType();
            if (!StringUtils.hasText(attachmentFileType) || !fileTypeList.contains(attachmentFileType.toLowerCase())) {
                return false;
            }
        }
        // 文件大小过滤
        Long fileSize = attachment.getFileSize();
        if (fileSize != null) {
            if (dto.getMaxFileSize() != null && fileSize > dto.getMaxFileSize()) {
                return false;
            }
            if (dto.getMinFileSize() != null && fileSize < dto.getMinFileSize()) {
                return false;
            }
        }
        // 路径匹配过滤
        if (pathPattern != null && StringUtils.hasText(attachment.getFilePath())) {
            String fullPath = Paths.get(attachment.getFilePath()).resolve(
                    StringUtils.hasText(attachment.getFileName()) ? attachment.getFileName() : ""
            ).toString();
            return pathPattern.matcher(fullPath).matches();
        }
        return true;
    }

    /**
     * 删除文件，支持重试
     */
    private void deleteFileWithRetry(String relativePath, int retryCount) {
        int attempts = 0;
        Exception lastException = null;
        while (attempts <= retryCount) {
            try {
                fileService.deleteIfExists(relativePath);
                return; // 删除成功，直接返回
            } catch (Exception e) {
                lastException = e;
                attempts++;
                if (attempts <= retryCount) {
                    log.warn("删除文件失败，准备重试，relativePath={}，第{}次尝试，错误：{}", relativePath, attempts, e.getMessage());
                }
            }
        }
        // 所有重试都失败，记录错误但不抛出异常（因为deleteIfExists本身应该处理文件不存在的情况）
        if (lastException != null) {
            log.error("删除文件失败，已重试{}次，relativePath={}，错误：{}", retryCount, relativePath, lastException.getMessage());
        }
    }
}