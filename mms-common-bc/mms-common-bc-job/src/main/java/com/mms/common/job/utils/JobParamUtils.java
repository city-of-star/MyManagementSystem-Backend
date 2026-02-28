package com.mms.common.job.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * 实现功能【任务参数解析工具类】
 * <p>
 * 业界常见做法：可选参数 + 默认值。JSON 可省略字段（用 DTO 默认值），但拒绝未知字段（防拼写错误）。
 * 详见 docs/任务参数设计规范.md
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-28 10:01:26
 */
@Slf4j
public class JobParamUtils {

    /**
     * JSON 解析工具（线程安全）
     * 配置：拒绝未知属性（多字段/拼写错误报错），允许省略字段（使用 DTO 默认值）
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

    /**
     * 将JSON字符串解析为指定的DTO对象
     * <p>
     * 规则：可省略字段（用默认值）、可传 null、拒绝未知字段、空对象 {} 合法
     * </p>
     *
     * @param paramsJson JSON参数字符串（空对象 {} 表示全部使用默认值）
     * @param clazz      目标DTO类型
     * @param <T>        目标DTO类型
     * @return 解析后的DTO对象
     * @throws IllegalArgumentException JSON 为空、格式错误、类型错误、存在未知字段时抛出，消息会返回给前端
     */
    public static <T> T parseParams(String paramsJson, Class<T> clazz) {
        if (!StringUtils.hasText(paramsJson)) {
            throw new IllegalArgumentException("任务参数JSON不能为空，请检查任务配置");
        }
        try {
            return OBJECT_MAPPER.readValue(paramsJson, clazz);
        } catch (JsonProcessingException e) {
            log.warn("任务参数JSON解析失败，paramsJson={}，目标类型={}，错误：{}", paramsJson, clazz.getSimpleName(), e.getMessage());
            throw new BusinessException(ErrorCode.PARAM_INVALID, "任务参数配置错误，请检查JSON格式及字段拼写：" + e.getMessage());
        }
    }

    /**
     * 私有构造函数，防止实例化
     */
    private JobParamUtils() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }
}
