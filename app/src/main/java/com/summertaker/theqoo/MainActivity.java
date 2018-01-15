package com.summertaker.theqoo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.summertaker.theqoo.article.ArticleListActivity;
import com.summertaker.theqoo.common.BaseActivity;
import com.summertaker.theqoo.common.BaseApplication;
import com.summertaker.theqoo.data.Site;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_PERMISSION_CODE = 100;

    private int mNavId = 0;

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mContext = MainActivity.this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);

                if (mNavId == R.id.nav_login) {
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                } else if (mNavId == R.id.nav_cache) {
                    Intent intent = new Intent(mContext, CacheActivity.class);
                    startActivity(intent);
                }
                mNavId = 0;
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mListView = findViewById(R.id.listView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Site site = (Site) adapterView.getItemAtPosition(i);
                //Log.e(mTag, site.getName());

                Intent intent = new Intent(mContext, ArticleListActivity.class);
                intent.putExtra("title", site.getTitle());
                intent.putExtra("url", site.getUrl());
                startActivity(intent);
            }
        });

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListView.setSelection(0);
                //mListView.smoothScrollToPosition(0);
                //mListView.setSelectionAfterHeaderView();
            }
        });

        loadData();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //int id = item.getItemId();

        //switch (id) {
        //    case R.id.nav_settings:
        //        break;
        //}

        mNavId = item.getItemId();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void loadData() {
        ArrayList<Site> sites = BaseApplication.getInstance().getSites();

        MainAdapter adapter = new MainAdapter(this, sites);

        mListView.setAdapter(adapter);
    }
}
