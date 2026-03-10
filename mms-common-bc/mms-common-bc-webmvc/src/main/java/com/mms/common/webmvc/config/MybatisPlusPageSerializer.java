package com.mms.common.webmvc.config;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 实现功能【MyBatis-Plus Page 序列化器】
 * <p>
 * - 项目全局开启 Long -> String（解决雪花 ID 前端精度问题）
 * - 但分页参数 total/current/size 明确是“小数字”，希望保持为 number（int），避免前端到处 Number() 转换
 * <p>
 *
 * @author li.hongyu
 * @date 2026-03-10 17:34:56
 */
public class MybatisPlusPageSerializer extends JsonSerializer<Page<?>> {

    @Override
    public void serialize(Page<?> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }
        gen.writeStartObject();
        // records
        gen.writeFieldName("records");
        serializers.defaultSerializeValue(value.getRecords(), gen);
        // total/size/current 输出为 int
        gen.writeNumberField("total", safeToInt(value.getTotal()));
        gen.writeNumberField("size", safeToInt(value.getSize()));
        gen.writeNumberField("current", safeToInt(value.getCurrent()));
        gen.writeEndObject();
    }

    private int safeToInt(long v) {
        if (v > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (v < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return (int) v;
    }
}

