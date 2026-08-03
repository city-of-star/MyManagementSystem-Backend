package com.mms.usercenter.service.message.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

/**
 * 实现功能【公告 HTML 净化工具】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
public final class MsgHtmlSanitizeUtils {

    private static final Safelist SAFELIST = Safelist.relaxed()
            .addTags("h1", "h2", "h3")
            .addAttributes(":all", "class")
            .addProtocols("a", "href", "http", "https", "mailto");

    private MsgHtmlSanitizeUtils() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }

    public static String sanitize(String html) {
        if (html == null || html.isBlank()) {
            return "";
        }
        return Jsoup.clean(html, "", SAFELIST, new Document.OutputSettings().prettyPrint(false));
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
