package com.mms.common.document.service.impl;

import com.alibaba.excel.EasyExcel;
import com.mms.common.document.service.ExcelExportService;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 实现功能【Excel导出服务实现类】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-03-25 16:10:00
 */
@Slf4j
public class ExcelExportServiceImpl implements ExcelExportService {

    @Override
    public <T> byte[] exportToBytes(String sheetName, Class<T> headClazz, List<T> dataList) {
        if (headClazz == null) {
            throw new IllegalArgumentException("headClazz 不能为空");
        }
        String finalSheetName = (sheetName == null || sheetName.isBlank()) ? "Sheet1" : sheetName.trim();
        List<T> safeDataList = Objects.requireNonNullElseGet(dataList, ArrayList::new);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            EasyExcel.write(outputStream, headClazz)
                    .sheet(finalSheetName)
                    .doWrite(safeDataList);
            return outputStream.toByteArray();
        } catch (Exception ex) {
            log.error("导出 Excel 失败，sheetName={}, headClazz={}", finalSheetName, headClazz.getName(), ex);
            throw new IllegalStateException("导出 Excel 失败", ex);
        }
    }
}

