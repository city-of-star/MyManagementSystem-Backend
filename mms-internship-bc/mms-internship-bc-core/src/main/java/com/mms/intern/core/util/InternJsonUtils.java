package com.mms.intern.core.util;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;

import java.util.Collections;
import java.util.List;

public final class InternJsonUtils {

    private InternJsonUtils() {
    }

    public static String toJsonArray(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return null;
        }
        return JSONUtil.toJsonStr(ids);
    }

    public static List<Long> parseLongList(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            JSONArray array = JSONUtil.parseArray(json);
            return JSONUtil.toList(array, Long.class);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
