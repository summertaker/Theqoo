package com.summertaker.theqoo.article;

import com.summertaker.theqoo.data.Article;

public interface ArticleListInterface {

    public void onTitleClick(Article article);

    public void onShareClick(Article article);

    public void onImageClick(Article article, String imageUrl);
}
