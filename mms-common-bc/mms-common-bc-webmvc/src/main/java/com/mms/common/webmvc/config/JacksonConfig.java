package com.mms.common.webmvc.config;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.mms.common.core.utils.JacksonObjectMapperUtils;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.math.BigInteger;
import java.util.List;

/**
 * 实现功能【Jackson 配置类】
 * <p>
 * 统一配置 Jackson 的日期时间序列化/反序列化格式
 * 处理 LocalDateTime、LocalDate、LocalTime、旧版java.util.Date 类型的日期格式转换
 * 处理 Long、long、BigInteger 类型再返回前端时序列化成字符串
 * </p>
 *
 * @author li.hongyu
 * @date 2026-01-20 14:11:28
 */
public class JacksonConfig {

    /**
     * 提供 WebMvcConfigurer：置顶自定义的 Jackson 转换器，确保统一 ObjectMapper 生效
     */
    public WebMvcConfigurer jacksonWebMvcConfigurer(List<Module> jacksonModules) {
        return new WebMvcConfigurer() {
            @Override
            public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
                // 移除已有 Jackson 转换器，避免顺序不确定
                converters.removeIf(c -> c instanceof MappingJackson2HttpMessageConverter);
                // 统一的 ObjectMapper
                ObjectMapper objectMapper = JacksonObjectMapperUtils.createCommonObjectMapper();
                // Long/long/BigInteger -> String
                SimpleModule numberAsString = new SimpleModule();
                numberAsString.addSerializer(Long.class, ToStringSerializer.instance);
                numberAsString.addSerializer(Long.TYPE, ToStringSerializer.instance);
                numberAsString.addSerializer(BigInteger.class, ToStringSerializer.instance);
                objectMapper.registerModule(numberAsString);
                // MyBatis-Plus Page：total/size/current 输出为 int，records 走 ObjectMapper 默认属性序列化链
                SimpleModule pageModule = new SimpleModule();
                @SuppressWarnings("unchecked")
                Class<Page<?>> pageClass = (Class<Page<?>>) (Class<?>) Page.class;
                pageModule.addSerializer(pageClass, new MybatisPlusPageSerializer());
                objectMapper.registerModule(pageModule);
                // 注册容器内的其它模块（Page、自定义属性级 Long 模块等）
                if (jacksonModules != null) {
                    for (Module module : jacksonModules) {
                        try {
                            objectMapper.registerModule(module);
                        } catch (Exception ignore) {
                            // 跳过重复或不兼容模块
                        }
                    }
                }
                // 置顶自定义 Jackson 转换器
                converters.add(0, new MappingJackson2HttpMessageConverter(objectMapper));
            }
        };
    }
}
