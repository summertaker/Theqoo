package com.summertaker.theqoo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.summertaker.theqoo.common.BaseDataAdapter;
import com.summertaker.theqoo.common.Config;
import com.summertaker.theqoo.data.Article;
import com.summertaker.theqoo.data.Site;
import com.summertaker.theqoo.util.ProportionalImageView;
import com.summertaker.theqoo.util.Util;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainAdapter extends BaseDataAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<Site> mSites = null;

    public MainAdapter(Context context, ArrayList<Site> sites) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mSites = sites;
    }

    @Override
    public int getCount() {
        return mSites.size();
    }

    @Override
    public Object getItem(int position) {
        return mSites.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final MainAdapter.ViewHolder holder;
        final Site site = mSites.get(position);

        if (convertView == null) {
            holder = new MainAdapter.ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.main_item, null);

            holder.tvTitle = convertView.findViewById(R.id.tvTitle);

            convertView.setTag(holder);
        } else {
            holder = (MainAdapter.ViewHolder) convertView.getTag();
        }

        holder.tvTitle.setText(site.getTitle());

        return convertView;
    }

    static class ViewHolder {
        TextView tvTitle;
    }
}

