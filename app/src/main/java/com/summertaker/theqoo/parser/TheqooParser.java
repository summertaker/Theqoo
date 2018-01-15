package com.summertaker.theqoo.parser;

import android.util.Log;

import com.summertaker.theqoo.common.BaseParser;
import com.summertaker.theqoo.data.Article;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TheqooParser extends BaseParser {

    public void parseList(String html, ArrayList<Article> articles) {
        if (html == null || html.isEmpty()) {
            return;
        }

        Document doc = Jsoup.parse(html);

        Element root = doc.select(".list").first();
        if (root == null) {
            return;
        }
        //Log.e(mTag, "root.html()...\n" + root.html());

        for (Element li : root.select("li")) {
            String title = "";
            String date = "";
            String url = "";
            int comment = 0;

            Element el;

            Element a = li.select("a").first();
            if (a == null) {
                continue;
            }
            //Log.e(mTag, a.outerHtml());

            url = "http://theqoo.net" + a.attr("href");

            Element ul = li.select("ul").first();
            if (ul == null) {
                continue;
            }
            el = ul.select(".title").first();
            el = el.select("span").first();
            title = el.text();

            el = ul.select(".date").first();
            date = el.text();

            el = li.select(".reply").first();
            if (el != null) {
                comment = Integer.parseInt(el.text().trim());
                title = title + " (" + comment + ")";
            }

            //Log.e(mTag, title + " " + url);

            Article article = new Article();
            article.setTitle(title);
            article.setDate(date);
            article.setComment(comment);
            article.setUrl(url);

            articles.add(article);
        }
    }

    public void parseDetail(String html, Article article) {
        if (html == null || html.isEmpty()) {
            return;
        }

        //Log.e(mTag, article.getTitle());

        Document doc = Jsoup.parse(html);

        Element root = doc.select(".xe_content").first();
        if (root == null) {
            return;
        }
        //Log.e(mTag, root.html());

        String content = root.html();

        article.setContent(content);

        boolean download = false;

        ArrayList<String> images = new ArrayList<>();
        for (Element img : root.select("img")) {
            String src = img.attr("src");
            //Log.e(mTag, "src: " + src);

            // https://ssl.pstatic.net/static/pwe/nm/btn_savepc.png // 다운로드 아이콘
            // http://mail1.daumcdn.net/mail_static/mint/img/big/img_down.png
            if (src.contains("pstatic.net") || src.contains("daumcdn.net")) {
                download = true;
                continue;
            }

            if (src.toLowerCase().contains(".gif")) {
                continue;
            }

            images.add(src);

            content = content.replaceAll(img.outerHtml(), "");
        }

        article.setDownload(download);

        //------------------------------------------------
        // 이미지 텍스트 URL
        //------------------------------------------------
        String regex = "http://img\\.theqoo\\.net/([^\\s|^<|^\\.]+)"; // \w : 알파벳이나 숫자
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);   // get a matcher object
        while (matcher.find()) {
            String url = matcher.group();
            //Log.e(mTag, "url: " + url);

            String id = matcher.group(1);
            //Log.e(mTag, "id: " + id);

            String src = "http://img.theqoo.net/img/" + id + ".jpg";
            //Log.e(mTag, "src: " + src);

            images.add(src);
        }

        article.setImages(images);

        parseYoutube(root, content, images);

        article.setTwitter(content.contains("twitter.com"));
        article.setInstagram(content.contains("instagram.com"));
        boolean isVideo = content.contains("youtube.com") || content.contains("nmv.naver.com");
        article.setVideo(isVideo);
    }
}

