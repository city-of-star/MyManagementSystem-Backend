package com.mms.common.document.service;

import java.util.List;

/**
 * 实现功能【Excel导出服务】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-03-25 16:10:00
 */
public interface ExcelExportService {

    /**
     * 导出 Excel 内容为字节数组
     *
     * @param sheetName 工作表名称
     * @param headClazz 表头模型类型（通常是导出 VO）
     * @param dataList  导出数据
     * @param <T>       数据泛型
     * @return Excel 文件字节数组
     */
    <T> byte[] exportToBytes(String sheetName, Class<T> headClazz, List<T> dataList);
}

