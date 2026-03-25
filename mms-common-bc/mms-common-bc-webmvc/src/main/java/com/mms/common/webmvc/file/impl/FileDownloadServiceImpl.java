package com.mms.common.webmvc.file.impl;

import com.mms.common.core.exceptions.ServerException;
import com.mms.common.webmvc.file.FileDownloadService;
import jakarta.servlet.http.HttpServletResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 实现功能【文件下载服务实现类】
 * <p>
 * 负责设置响应头并将二进制文件内容写入响应流。
 * <p>
 *
 * @author li.hongyu
 * @date 2026-03-25 11:21:36
 */
public class FileDownloadServiceImpl implements FileDownloadService {

    private static final String EXCEL_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @Override
    public void writeAttachment(HttpServletResponse response, byte[] content, String fileName, String contentType) {
        if (response == null) {
            throw new IllegalArgumentException("HttpServletResponse 不能为空");
        }
        if (content == null) {
            throw new IllegalArgumentException("文件内容不能为空");
        }
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("contentType 不能为空");
        }
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);
        response.setContentLength(content.length);
        try {
            response.getOutputStream().write(content);
            response.getOutputStream().flush();
        } catch (Exception e) {
            throw new ServerException("文件下载写出失败", e);
        }
    }

    @Override
    public void writeExcel(HttpServletResponse response, byte[] content, String fileName) {
        String excelFileName = (fileName == null || fileName.isBlank()) ? "export.xlsx" : fileName;
        String finalFileName = excelFileName.endsWith(".xlsx") ? excelFileName : excelFileName + ".xlsx";
        writeAttachment(response, content, finalFileName, EXCEL_CONTENT_TYPE);
    }
}