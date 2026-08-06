package com.mms.usercenter.service.message.utils;

import com.mms.common.core.exceptions.ServerException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

/**
 * 实现功能【公告 HTML 净化工具】
 * <p>
 * 基于 jsoup 对公告富文本做白名单清洗，并支持抽取纯文本摘要
 * 白名单懒加载，避免静态初始化失败后整类永久不可用（NoClassDefFoundError: Could not initialize class）
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
public final class MsgHtmlSanitizeUtils {

    /** 富文本白名单（双重检查懒加载） */
    private static volatile Safelist safelist;

    /**
     * 获取并按需初始化 HTML 白名单（relaxed + h1–h3、class、mailto 等）
     */
    private static Safelist safelist() {
        Safelist local = safelist;
        if (local == null) {
            synchronized (MsgHtmlSanitizeUtils.class) {
                local = safelist;
                if (local == null) {
                    try {
                        local = Safelist.relaxed()
                                .addTags("h1", "h2", "h3")
                                .addAttributes(":all", "class")
                                .addProtocols("a", "href", "http", "https", "mailto");
                        safelist = local;
                    } catch (Throwable ex) {
                        throw new ServerException("公告内容净化组件不可用，请检查 jsoup 依赖: " + ex.getMessage(), ex);
                    }
                }
            }
        }
        return local;
    }

    /**
     * 按白名单净化 HTML，移除脚本等高风险内容；null/空白返回空串
     */
    public static String sanitize(String html) {
        if (html == null || html.isBlank()) {
            return "";
        }
        return Jsoup.clean(html, "", safelist(), new Document.OutputSettings().prettyPrint(false));
    }

    /**
     * 从 HTML 抽取纯文本；maxLen>0 时截断，否则不截断
     */
    public static String toPlainText(String html, int maxLen) {
        if (html == null || html.isBlank()) {
            return "";
        }
        String text = Jsoup.parse(html).text();
        if (maxLen > 0 && text.length() > maxLen) {
            return text.substring(0, maxLen);
        }
        return text;
    }

    private MsgHtmlSanitizeUtils() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }
}
