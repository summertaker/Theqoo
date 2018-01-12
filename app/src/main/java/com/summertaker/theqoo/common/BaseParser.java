package com.summertaker.theqoo.common;

import com.summertaker.theqoo.data.Article;

import java.util.ArrayList;

public class BaseParser {

    protected String mTag;

    public BaseParser() {
        mTag = "== " + this.getClass().getSimpleName();
    }

    public void parseData(String html, ArrayList<Article> teamList) {

    }
}
