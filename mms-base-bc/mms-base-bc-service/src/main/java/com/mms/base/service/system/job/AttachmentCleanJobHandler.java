package com.mms.base.service.system.job;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mms.base.common.system.entity.AttachmentEntity;
import com.mms.base.service.file.service.FileService;
import com.mms.base.service.system.mapper.AttachmentMapper;
import com.mms.job.common.annotation.JobDefinition;
import com.mms.job.common.enums.JobTypeEnum;
import com.mms.job.core.JobHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
@JobDefinition(JobTypeEnum.ATTACHMENT_CLEAN)
public class AttachmentCleanJobHandler implements JobHandler {

    @Resource
    private AttachmentMapper attachmentMapper;

    @Resource
    private FileService fileService;

    /**
     * 简单使用局部 ObjectMapper 解析参数
     * （如需统一配置，可后续改为注入全局 ObjectMapper）
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(String paramsJson) {
        int batchSize = resolveBatchSize(paramsJson);
        log.info("开始执行附件清理任务，batchSize={}", batchSize);

        // 查询一批已逻辑删除的附件记录
        List<AttachmentEntity> records = attachmentMapper.selectDeletedForClean(batchSize);

        if (records == null || records.isEmpty()) {
            log.info("本次附件清理任务无待清理记录");
            return;
        }

        log.info("本次附件清理任务共需处理记录数：{}", records.size());
        for (AttachmentEntity attachment : records) {
            try {
                if (!StringUtils.hasText(attachment.getFilePath()) || !StringUtils.hasText(attachment.getFileName())) {
                    log.warn("附件记录缺少 filePath/fileName，跳过物理文件删除，仅删除数据库记录，attachmentId={}", attachment.getId());
                    attachmentMapper.hardDeleteById(attachment.getId());
                    continue;
                }
                // 组装相对文件路径（与 AttachmentServiceImpl 中逻辑一致）
                Path relativePath = Paths.get(attachment.getFilePath()).resolve(attachment.getFileName());
                // 删除物理文件（不存在则忽略）
                fileService.deleteIfExists(relativePath.toString());
                // 物理删除数据库记录
                attachmentMapper.hardDeleteById(attachment.getId());
                log.info("附件清理成功，attachmentId={}，path={}", attachment.getId(), relativePath);
            } catch (Exception e) {
                // 单条异常不影响其他记录的清理
                log.error("附件清理失败，attachmentId={}，错误：{}", attachment.getId(), e.getMessage(), e);
            }
        }
    }

    /**
     * 解析批次大小参数，解析失败或未配置时返回默认值
     */
    private int resolveBatchSize(String paramsJson) {
        int defaultSize = 100;
        if (!StringUtils.hasText(paramsJson)) {
            return defaultSize;
        }
        try {
            JsonNode root = objectMapper.readTree(paramsJson);
            JsonNode node = root.get("batchSize");
            if (node != null && node.canConvertToInt()) {
                int size = node.asInt();
                return size > 0 ? size : defaultSize;
            }
        } catch (Exception e) {
            log.warn("解析附件清理任务参数失败，将使用默认 batchSize={}，paramsJson={}", defaultSize, paramsJson, e);
        }
        return defaultSize;
    }
}