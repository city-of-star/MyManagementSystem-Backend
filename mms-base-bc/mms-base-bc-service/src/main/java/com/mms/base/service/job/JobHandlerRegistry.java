package com.mms.base.service.job;

import com.mms.base.service.system.job.AttachmentCleanJobHandler;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 实现功能【定时任务处理器注册中心】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-25 11:07:06
 */
@Slf4j
@Component
@AllArgsConstructor
public class JobHandlerRegistry {

    // 定时服务处理器集合
    private final Map<String, JobHandler> handlerMap = new HashMap<>();

    // 附件清理定时服务处理器
    private final AttachmentCleanJobHandler attachmentCleanJobHandler;

    /**
     * 初始化注册所有定时任务处理器
     */
    @PostConstruct
    public void init() {
        register(JobTypeEnum.ATTACHMENT_CLEAN, attachmentCleanJobHandler);
    }

    /**
     * 按枚举注册定时任务处理器
     *
     * @param jobTypeEnum 任务类型枚举
     * @param handler     任务处理器
     */
    public void register(JobTypeEnum jobTypeEnum, JobHandler handler) {
        if (jobTypeEnum == null || handler == null) {
            return;
        }
        String jobType = jobTypeEnum.getType();
        String jobDescription = jobTypeEnum.getDescription();
        handlerMap.put(jobType, handler);
        log.info("注册定时任务处理器：jobType={}，handler={}", jobType, jobDescription);
    }

    /**
     * 根据任务编码获取对应处理器
     *
     * @param jobType 任务编码
     * @return 处理器实例，找不到时返回 null
     */
    public JobHandler getHandler(String jobType) {
        if (jobType == null || jobType.isEmpty()) {
            return null;
        }
        return handlerMap.get(jobType);
    }
}