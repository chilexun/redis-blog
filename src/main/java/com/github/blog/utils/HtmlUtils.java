package com.github.blog.utils;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlUtils {

    private final static String regxpForHtmlTag = "<([^>]*)>"; 
    private final static Pattern htmlPattern = Pattern.compile(regxpForHtmlTag);

    public static String filterOffAllTag(String str) {
        if(StringUtils.isBlank(str))
            return str;

        Matcher matcher = htmlPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        boolean result = matcher.find();
        while (result) {
            matcher.appendReplacement(sb, "");
            result = matcher.find();
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String filterOffTag(String str, String tag) {
        if(StringUtils.isAnyBlank(str, tag))
            return str;

        String regxp = "<\\s*" + tag + "\\s+([^>]*)\\s*>";
        Pattern pattern = Pattern.compile(regxp);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        boolean result1 = matcher.find();
        while (result1) {
            matcher.appendReplacement(sb, "");
            result1 = matcher.find();
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 过滤html中的标签，只保留安全标签
     * @param html
     * @return
     */
    public static String getSafeBody(String html){
        if(StringUtils.isBlank(html))
            return html;
        Document doc = Jsoup.parse(html);
        Cleaner cleaner = new Cleaner(Whitelist.relaxed());
        Document clean = cleaner.clean(doc);
        clean.outputSettings().prettyPrint(false);
        return clean.body().html();
    }

    /**
     * 获取HTML body，并过滤所有html标签
     * @param html
     * @return
     */
    public static String getBodyText(String html){
        if(StringUtils.isBlank(html))
            return html;
        Document doc = Jsoup.parse(html);
        return doc.body().text();
    }
}
