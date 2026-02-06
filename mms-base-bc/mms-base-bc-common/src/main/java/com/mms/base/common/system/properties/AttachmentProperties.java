package com.mms.base.common.system.properties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现功能【附件配置】
 * <p>
 * 配置项示例（Nacos/YAML）（见 base-DEV.yaml）：
 * <pre>
 * file:
 *   upload:
 *     allowed-types: [jpg, png, pdf]
 *     max-size: 50
 *     storage-path: ./attachments
 *     url-prefix: /base/api/attachment/stream
 *     enable-type-validation: true
 *     naming-strategy: uuid
 * </pre>
 * </p>
 *
 * @author li.hongyu
 * @date 2026-02-06
 */
@Data
@Component
@ConfigurationProperties(prefix = "file.upload")
@Schema(description = "附件相关配置")
public class AttachmentProperties {

    /**
     * 允许的文件类型（扩展名，小写）
     */
    @Schema(description = "允许的文件类型（扩展名，小写）", example = "[\"jpg\",\"png\",\"pdf\"]")
    private List<String> allowedTypes = new ArrayList<>();

    /**
     * 单个文件最大大小（MB）
     */
    @Schema(description = "单个文件最大大小（MB）", example = "50")
    private Integer maxSize = 50;

    /**
     * 存储路径（相对路径或绝对路径）
     */
    @Schema(description = "存储路径", example = "./attachments")
    private String storagePath = "./attachments";

    /**
     * 静态资源映射公开路径（可选）
     * <p>
     * 为空表示不启用静态资源映射；若配置为 /attachment-files，则可通过 /attachment-files/** 直接访问本地文件。
     * </p>
     */
    @Schema(description = "静态资源映射公开路径（可选）", example = "/attachment-files")
    private String staticPublicPath = "";

    /**
     * 访问URL前缀（通常指向本服务的 stream 接口，由网关转发）
     */
    @Schema(description = "访问URL前缀", example = "/base/api/attachment/stream")
    private String urlPrefix = "/base/api/attachment/stream";

    /**
     * 是否启用文件类型验证（MIME类型 + 扩展名双重验证）
     */
    @Schema(description = "是否启用文件类型验证", example = "true")
    private Boolean enableTypeValidation = true;

    /**
     * 文件命名策略：uuid-使用UUID，timestamp-使用时间戳，original-保留原名
     */
    @Schema(description = "文件命名策略", example = "uuid")
    private String namingStrategy = "uuid";
}

