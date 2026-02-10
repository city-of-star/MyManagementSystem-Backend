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
     * 删除文件（如果文件不存在则返回 false，不抛异常）
     *
     * @param relativePath 相对存储路径（如 2026/02/09/xxx.png）
     */
    void deleteIfExists(String relativePath);

    /**
     * 根据相对路径打开文件输入流（由调用方负责关闭）
     *
     * @param relativePath 相对存储路径（如 2026/02/09/xxx.png）
     * @return 文件输入流
     */
    InputStream openStream(String relativePath) throws IOException;

    /**
     * 获取文件大小（字节）
     *
     * @param relativePath 相对存储路径
     * @return 文件大小（字节）
     */
    long getFileSize(String relativePath) throws IOException;

    /**
     * 获取文件的 Content-Type
     *
     * @param relativePath 相对存储路径
     * @return MIME 类型，无法判断时返回 null
     */
    String getContentType(String relativePath) throws IOException;

    /**
     * 判断文件是否存在且为普通文件
     *
     * @param relativePath 相对存储路径
     * @return 是否存在
     */
    boolean exists(String relativePath);
}