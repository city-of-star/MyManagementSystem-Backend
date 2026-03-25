package com.mms.common.webmvc.file;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 实现功能【文件下载服务】
 * <p>
 * 统一封装文件下载响应头与输出流写出逻辑，避免各 Controller 重复实现。
 * <p>
 *
 * @author li.hongyu
 * @date 2026-03-25 11:20:52
 */
public interface FileDownloadService {

    /**
     * 写出通用附件下载响应
     *
     * @param response    Http 响应对象
     * @param content     文件二进制内容
     * @param fileName    下载文件名
     * @param contentType 文件 MIME 类型
     */
    void writeAttachment(HttpServletResponse response, byte[] content, String fileName, String contentType);

    /**
     * 写出 Excel 下载响应（xlsx）
     *
     * @param response Http 响应对象
     * @param content  文件二进制内容
     * @param fileName 下载文件名
     */
    void writeExcel(HttpServletResponse response, byte[] content, String fileName);
}