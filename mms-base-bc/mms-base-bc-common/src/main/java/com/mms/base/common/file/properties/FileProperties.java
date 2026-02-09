package com.mms.base.common.file.properties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现功能【文件存储配置】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-02-06 16:00:21
 */
@Data
@Component
@ConfigurationProperties(prefix = "file.upload")
@Schema(description = "文件存储配置")
public class FileProperties {

    @Schema(description = "允许的文件类型（扩展名，小写）", example = "[\"jpg\",\"png\",\"pdf\"]")
    private List<String> allowedTypes = new ArrayList<>();

    @Schema(description = "单个文件最大大小（MB）", example = "50")
    private Integer maxSize = 50;

    @Schema(description = "存储路径", example = "./attachments")
    private String storagePath = "./attachments";

    @Schema(description = "静态资源映射公开路径（可选）", example = "/attachment-files")
    private String staticPublicPath = "";

    @Schema(description = "访问URL前缀", example = "/base/api/attachment/stream")
    private String urlPrefix = "/base/api/attachment/stream";
}

