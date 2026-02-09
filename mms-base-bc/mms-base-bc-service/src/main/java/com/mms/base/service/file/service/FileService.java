package com.mms.base.service.file.service;

import com.mms.base.common.file.vo.FileVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * 实现功能【文件服务】
 * <p>
 * 负责文件物理存储与读取，不关心业务表结构
 *
 * @author li.hongyu
 * @date 2026-02-09 15:27:27
 */
public interface FileService {

    /**
     * 存储上传文件，返回存储结果（路径、URL、大小、类型等）
     */
    FileVo store(MultipartFile file) throws IOException;

    /**
     * 按相对路径读取文件（用于下载/预览）
     */
    InputStream load(String relativePath) throws IOException;
}