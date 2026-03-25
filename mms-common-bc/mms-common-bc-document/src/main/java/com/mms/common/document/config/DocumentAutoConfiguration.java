package com.mms.common.document.config;

import com.mms.common.document.service.impl.ExcelExportServiceImpl;
import com.mms.common.document.service.ExcelExportService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 实现功能【文档模块自动装配配置】
 * <p>
 * 提供文档导入导出相关基础能力
 * <p>
 *
 * @author li.hongyu
 * @date 2026-03-25 16:10:00
 */
@Configuration
public class DocumentAutoConfiguration {

    /**
     * 创建 Excel 导出服务 Bean
     */
    @Bean
    @ConditionalOnMissingBean(ExcelExportService.class)
    public ExcelExportService excelExportService() {
        return new ExcelExportServiceImpl();
    }
}

