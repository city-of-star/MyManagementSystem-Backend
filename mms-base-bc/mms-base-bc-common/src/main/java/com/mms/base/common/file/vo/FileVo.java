package com.mms.base.common.file.vo;

import lombok.Data;

/**
 * 实现功能【文件存储结果】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-09 15:27:27
 */
@Data
public class FileVo {

    /**
     * 原始文件名
     */
    private String originalFileName;

    /**
     * 实际存储的文件名（不含路径）
     */
    private String storedFileName;

    /**
     * 相对路径，例如：2026/02/09/
     */
    private String relativePath;

    /**
     * 完整访问 URL（包含前缀）
     */
    private String fileUrl;

    /**
     * 文件大小（字节）
     */
    private long size;

    /**
     * 扩展名（小写）
     */
    private String fileType;

    /**
     * MIME 类型
     */
    private String mimeType;
}

