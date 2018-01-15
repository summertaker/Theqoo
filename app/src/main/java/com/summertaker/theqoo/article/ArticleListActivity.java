package com.summertaker.theqoo.article;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.summertaker.theqoo.R;
import com.summertaker.theqoo.common.BaseActivity;
import com.summertaker.theqoo.common.BaseApplication;
import com.summertaker.theqoo.common.Config;
import com.summertaker.theqoo.data.Article;
import com.summertaker.theqoo.parser.TheqooParser;
import com.summertaker.theqoo.util.EndlessScrollListener;
import com.summertaker.theqoo.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ArticleListActivity extends BaseActivity implements ArticleListInterface {

    private int mPage = 1;
    private boolean mIsLoading = false;
    private boolean mHasMoreData = true;

    private int mArticleIndex = 0;

    private LinearLayout mLoLoading;
    private LinearLayout mLoLoadMore;

    private String mUrl = "";

    private TheqooParser mParser;
    private ArrayList<Article> mAllArticles;
    private ArrayList<Article> mPageArticles;
    private ArticleListAdapter mAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_list_activity);

        mContext = ArticleListActivity.this;

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        mUrl = intent.getStringExtra("url");

        initToolbar(title);

        mLoLoading = findViewById(R.id.loLoading);
        mLoLoadMore = findViewById(R.id.loLoadMore);

        mParser = new TheqooParser();

        mAllArticles = new ArrayList<>();

        mListView = findViewById(R.id.listView);
        mListView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                //Log.e(mTag, "onLoadMore()... " + mPage + " Page.");

                if (mHasMoreData && !mIsLoading) {
                    mLoLoadMore.setVisibility(View.VISIBLE);
                    loadList();
                    return true; // ONLY if more data is actually being loaded; false otherwise.
                } else {
                    return false;
                }
            }
        });

        loadList();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void onToolbarClick() {
        mListView.setSelection(0);
    }

    private void loadList() {
        if (mIsLoading) {
            return;
        }
        mIsLoading = false;

        String url = mUrl + mPage;

        requestData(url);
    }

    private void loadDetail() {
        if (mIsLoading) {
            return;
        }
        mIsLoading = false;

        if (mArticleIndex < mPageArticles.size()) {
            String url = mPageArticles.get(mArticleIndex).getUrl();
            requestData(url);
        } else {
            renderData();
        }
    }

    private void requestData(final String url) {
        //Log.e(mTag, url);

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.e(mTag, "requestData().onResponse()...\n" + response);
                parseData(url, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(mTag, "ERROR: " + error.networkResponse.statusCode);
                Util.alert(mContext, getString(R.string.error), error.networkResponse.statusCode + ": " + error.toString(), null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                //headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", Config.USER_AGENT_MOBILE);

                //headers.put("PHPSESSID", "ckq3gf3niq9ush51vpvkp7s0nm");
                //headers.put("mobile", "true");
                //headers.put("user-agent", "154734bf181769af3bbb219f924580e0");
                //headers.put("xe_logged", "true");

                return headers;
            }
        };

        BaseApplication.getInstance().addToRequestQueue(strReq, mTag);
    }

    private void parseData(String url, String response) {
        if (response.isEmpty()) {
            Util.alert(mContext, getString(R.string.error), "Response is empty.", null);
        } else {
            if (url.contains("/index.php?")) {
                //-------------------
                // 글 목록 파싱하기
                //-------------------
                mPageArticles = new ArrayList<>();
                mParser.parseList(response, mPageArticles);
                //Log.e(mTag, "mPageArticles.size() = " + mPageArticles.size());

                if (mPageArticles.size() == 0) {
                    mHasMoreData = false;
                } else {
                    mAllArticles.addAll(mPageArticles);
                }
            } else {
                //-------------------
                // 글 상세 파싱하기
                //-------------------
                mParser.parseDetail(response, mPageArticles.get(mArticleIndex));
                mArticleIndex++;
            }

            loadDetail();
        }
    }

    private void renderData() {
        if (mAdapter == null) {
            mAdapter = new ArticleListAdapter(this, mAllArticles, this);
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }

        if (mPage == 1) {
            mLoLoading.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        } else {
            mLoLoadMore.setVisibility(View.GONE);
        }

        if (mHasMoreData) {
            mPage++;
        }

        mArticleIndex = 0;

        mIsLoading = false;
    }

    @Override
    public void onTitleClick(Article article) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl()));
        startActivity(intent);
    }

    @Override
    public void onShareClick(Article article) {
        String title = article.getTitle();
        String url = article.getUrl();

        //Log.e(mTag, title + " " + url);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, url);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, title));
    }

    @Override
    public void onImageClick(Article article, String imageUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(imageUrl));
        startActivity(intent);
    }
}
