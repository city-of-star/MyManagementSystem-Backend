package com.mms.usercenter.service.message.utils;

import com.mms.common.core.exceptions.ServerException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

/**
 * 实现功能【公告 HTML 净化工具】
 * <p>
 * 懒加载白名单，避免静态初始化失败后整类永久不可用（NoClassDefFoundError: Could not initialize class）。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
public final class MsgHtmlSanitizeUtils {

    private static volatile Safelist safelist;

    private MsgHtmlSanitizeUtils() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }

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

    public static String sanitize(String html) {
        if (html == null || html.isBlank()) {
            return "";
        }
        return Jsoup.clean(html, "", safelist(), new Document.OutputSettings().prettyPrint(false));
    }

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
}
