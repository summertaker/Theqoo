package com.summertaker.theqoo.common;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.summertaker.theqoo.R;

public class BaseActivity extends AppCompatActivity {

    protected String mTag = "== " + getClass().getSimpleName();
    protected Context mContext;
    protected Resources mResources;
    //protected SharedPreferences mSharedPreferences;
    //protected SharedPreferences.Editor mSharedEditor;

    protected Toolbar mBaseToolbar;
    protected ProgressBar mBaseProgressBar;

    protected void initToolbar(String title) {
        mContext = BaseActivity.this;
        mResources = mContext.getResources();

        //mSharedPreferences = getSharedPreferences(Config.USER_PREFERENCE_KEY, 0);
        //mSharedEditor = mSharedPreferences.edit();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);

            if (title != null) {
                actionBar.setTitle(title);
            }
        }

        if (toolbar != null) {
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onToolbarClick();
                }
            });
        }
    }

    protected void initToolbarProgressBar() {
        //int color = 0xffffffff;
        //mBaseProgressBar = (ProgressBar) findViewById(R.id.toolbar_progress_bar);
        //mBaseProgressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }

    protected void onToolbarClick() {
        //Util.alert(mContext, "Toolbar");
    }

    protected void showToolbarProgressBar() {
        mBaseProgressBar.setVisibility(View.VISIBLE);
    }

    protected void hideToolbarProgressBar() {
        mBaseProgressBar.setVisibility(View.GONE);
    }

    protected void doFinish() {
        //Intent intent = new Intent();
        //intent.putExtra("pictureId", mData.getGroupId());
        //setResult(ACTIVITY_RESULT_CODE, intent);

        finish();
    }
}

