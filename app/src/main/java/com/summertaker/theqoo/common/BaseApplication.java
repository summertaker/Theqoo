package com.summertaker.theqoo.common;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.summertaker.theqoo.data.Site;

import java.util.ArrayList;

public class BaseApplication extends Application {

    private static BaseApplication mInstance;

    public static final String TAG = BaseApplication.class.getSimpleName();

    private RequestQueue mRequestQueue;

    private ArrayList<Site> mSites = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        mSites.add(new Site("재팬스퀘어", "http://theqoo.net/index.php?mid=japan&filter_mode=normal&category=26063&page="));
        mSites.add(new Site("坂道46", "http://theqoo.net/index.php?mid=jdol&filter_mode=normal&category=29770&page="));
        mSites.add(new Site("48스퀘어", "http://theqoo.net/index.php?mid=talk48&filter_mode=normal&category=161632742&page="));
        mSites.add(new Site("48돌", "http://theqoo.net/index.php?mid=dol48&filter_mode=normal&page="));
        mSites.add(new Site("하로프로", "http://theqoo.net/index.php?mid=jdol&filter_mode=normal&category=26384&page="));
        mSites.add(new Site("플레이", "http://theqoo.net/index.php?_filter=search&mid=japan&category=26063&search_keyword=플레이보이&search_target=title&page="));
    }

    public static synchronized BaseApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public ArrayList<Site> getSites() {
        return mSites;
    }

    public void setSites(ArrayList<Site> sites) {
        this.mSites = sites;
    }
}
