package com.summertaker.theqoo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.summertaker.theqoo.common.BaseActivity;
import com.summertaker.theqoo.common.BaseApplication;
import com.summertaker.theqoo.common.Config;
import com.summertaker.theqoo.util.Util;

import java.net.CookieHandler;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends BaseActivity {

    private WebView mWebView;

    private String mId = "summertaker@gmail.com";
    private String mPassword = "dfsfer345";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        initToolbar(null);

        mWebView = findViewById(R.id.webView);

        //WebSettings webSettings = mWebView.getSettings();
        //webSettings.setJavaScriptEnabled(true);

        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.loadUrl("http://theqoo.net/index.php?mid=index&act=dispMemberLoginForm");

        //doLogin();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            String cookies = CookieManager.getInstance().getCookie(url);
            Log.e(mTag, ">> COOKIES\n" + cookies);
            /*
             PHPSESSID=ckq3gf3niq9ush51vpvkp7s0nm; mobile=true; user-agent=154734bf181769af3bbb219f924580e0; xe_logged=true
            */
        }
        /*
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().equals("www.example.com")) {
                // This is my web site, so do not override; let my WebView load the page
                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);

            return true;
        }
        */
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    private void doLogin() {
        String url = "http://theqoo.net/index.php?mid=index&act=dispMemberLoginForm";

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(mTag, ">> onResponse()\n" + response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(mTag, "ERROR: " + error.toString());
                Log.e(mTag, "ERROR: " + error.networkResponse.statusCode);
                Util.alert(mContext, getString(R.string.error), "CODE: " + error.networkResponse.statusCode, null);
            }
        }) {
            //@Override
            //protected Map<String, String> getParams() throws AuthFailureError {
            //    Map<String, String> params = new HashMap<>();
            //    //add params <key,value>
            //    return params;
            //}

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                //headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", Config.USER_AGENT_MOBILE);

                headers.put("error_return_url", "/index.php?mid=index&amp;act=dispMemberLoginForm");
                headers.put("mid", "index");
                headers.put("vid", "");
                headers.put("module", "member");
                headers.put("act", "procMemberLogin");
                headers.put("redirect_url", "/index");
                headers.put("xe_validator_id", "modules/member/m.skin/default/login_form/1");
                headers.put("user_id", mId);
                headers.put("password", mPassword);
                headers.put("keep_signed", "Y");

                //String credentials = mId + ":" + mPassword;
                //String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                //headers.put("Authorization", auth);

                return headers;
            }
        };

        BaseApplication.getInstance().addToRequestQueue(strReq, mTag);
    }
}
