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

    private final Map<String, JobHandler> handlerMap = new HashMap<>();

    private final AttachmentCleanJobHandler attachmentCleanJobHandler;

    /**
     * 容器启动完成后，注册所有内置任务处理器
     */
    @PostConstruct
    public void init() {
        register(JobCodeEnum.ATTACHMENT_CLEAN, attachmentCleanJobHandler);
    }

    /**
     * 按枚举注册任务处理器
     *
     * @param jobCodeEnum 任务编码枚举
     * @param handler     任务处理器
     */
    public void register(JobCodeEnum jobCodeEnum, JobHandler handler) {
        if (jobCodeEnum == null || handler == null) {
            return;
        }
        String jobCode = jobCodeEnum.getCode();
        handlerMap.put(jobCode, handler);
        log.info("注册定时任务处理器：jobCode={}，handler={}", jobCode, handler.getClass().getSimpleName());
    }

    /**
     * 根据任务编码获取对应处理器
     *
     * @param jobCode 任务编码
     * @return 处理器实例，找不到时返回 {@code null}
     */
    public JobHandler getHandler(String jobCode) {
        if (jobCode == null || jobCode.isEmpty()) {
            return null;
        }
        return handlerMap.get(jobCode);
    }
}