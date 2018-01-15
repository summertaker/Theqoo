package com.summertaker.theqoo.common;

import android.util.Log;

import com.summertaker.theqoo.data.Article;

import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseParser {

    protected String mTag;

    public BaseParser() {
        mTag = "== " + this.getClass().getSimpleName();
    }

    public void parseData(String html, ArrayList<Article> teamList) {

    }

    protected String parseYoutube(Element root, String content, ArrayList<String> medias) {
        String result = content;

        //------------------------------------------------
        // 유튜브 URL 목록 (1)
        //------------------------------------------------
        String regex = "https\\://www\\.youtube\\.com/watch\\?v=([_|\\-|\\w]+)"; // \\w : 알파벳이나 숫자
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(result);   // get a matcher object
        while (matcher.find()) {
            String url = matcher.group();
            //Log.e(mTag, "url: " + url);

            String id = matcher.group(1);
            String src = "https://img.youtube.com/vi/" + id + "/0.jpg";
            //Log.e(mTag, "src: " + src);

            //if (BaseApplication.getInstance().SETTINGS_USE_IMAGE_GETTER) {
            //    String search = "<iframe[^>|.]*src=\"" + regex + "\"[^>|.]*></iframe>";
            //    String replace = "<a href=\"https://m.youtube.com/watch?v=" + id + "\"><img src=\"" + src + "\"></a> <small><font color=\"#888888\">Youtube</font></small>";
            //    content = content.replaceAll(search, replace);
            //} else {

            medias.add(src);

            result = result.replace(url, "");
            //}
        }

        for (Element iframe : root.select("iframe")) {
            String url = iframe.attr("src");
            //Log.e(mTag, "url: " + url);

            regex = "https\\://www\\.youtube\\.com/embed/([_|\\-|\\w]+)"; // \\w : 알파벳이나 숫자
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(url);   // get a matcher object

            String src = null;
            while (matcher.find()) {
                String id = matcher.group(1);
                src = "https://img.youtube.com/vi/" + id + "/0.jpg";
                Log.e(mTag, "src: " + src);

                //if (BaseApplication.getInstance().SETTINGS_USE_IMAGE_GETTER) {
                //    String search = "<iframe[^>|.]*src=\"" + regex + "\"[^>|.]*></iframe>";
                //    String replace = "<a href=\"https://m.youtube.com/watch?v=" + id + "\"><img src=\"" + src + "\"></a> <small><font color=\"#888888\">Youtube</font></small>";
                //    content = content.replaceAll(search, replace);
                //} else {
                //}
            }
            if (src != null) {
                medias.add(src);

                //Log.e(mTag, "iframe.outerHtml(): " + iframe.outerHtml());
                result = result.replace(iframe.outerHtml(), "");
            }
        }

        return result;
    }
}
