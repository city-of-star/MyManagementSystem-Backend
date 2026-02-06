package com.mms.base.service.system.config;

import com.mms.base.common.system.properties.AttachmentProperties;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 实现功能【附件静态资源映射配置】
 * <p>
 * 将本地上传目录映射为可访问的 URL 路径（可选）。
 * <p>
 * 默认不启用，避免与 Controller 的 /attachment/stream/** 冲突。
 * 如需启用，请在 Nacos 中配置：
 * <pre>
 * file:
 *   upload:
 *     static-public-path: /attachment-files
 * </pre>
 * </p>
 *
 * @author li.hongyu
 * @date 2026-02-06
 */
@Configuration
public class AttachmentResourceConfig implements WebMvcConfigurer {

    @Resource
    private AttachmentProperties attachmentProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String publicPath = attachmentProperties.getStaticPublicPath();
        if (!StringUtils.hasText(publicPath)) {
            return;
        }
        if (!publicPath.startsWith("/")) {
            publicPath = "/" + publicPath;
        }
        if (publicPath.endsWith("/")) {
            publicPath = publicPath.substring(0, publicPath.length() - 1);
        }

        Path baseDir = Paths.get(attachmentProperties.getStoragePath()).toAbsolutePath().normalize();
        String location = baseDir.toUri().toString(); // file:/...

        registry.addResourceHandler(publicPath + "/**")
                .addResourceLocations(location);
    }
}

