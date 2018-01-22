package com.summertaker.theqoo.article;

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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.summertaker.theqoo.R;
import com.summertaker.theqoo.common.BaseDataAdapter;
import com.summertaker.theqoo.common.Config;
import com.summertaker.theqoo.data.Article;
import com.summertaker.theqoo.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ArticleListAdapter extends BaseDataAdapter {

    private Context mContext;
    private String mLocale;
    private LayoutInflater mLayoutInflater;
    private ArrayList<Article> mArticles = null;
    private boolean mIsCacheMode = true;

    private ArticleListInterface mArticleListInterface;

    private LinearLayout.LayoutParams mParams;
    private LinearLayout.LayoutParams mParamsNoMargin;

    public ArticleListAdapter(Context context, ArrayList<Article> articles, ArticleListInterface articleListInterface) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mArticles = articles;
        this.mArticleListInterface = articleListInterface;

        float density = mContext.getResources().getDisplayMetrics().density;
        int height = (int) (200 * density);
        int margin = (int) (1 * density);
        mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);
        mParams.setMargins(0, 0, margin, 0);
        mParamsNoMargin = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);
    }

    @Override
    public int getCount() {
        return mArticles.size();
    }

    @Override
    public Object getItem(int position) {
        return mArticles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ArticleListAdapter.ViewHolder holder;
        final Article article = mArticles.get(position);

        if (convertView == null) {
            holder = new ArticleListAdapter.ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.article_list_item, null);

            holder.hsImage = convertView.findViewById(R.id.hsImage);
            holder.loImage = convertView.findViewById(R.id.loImage);
            holder.ivPicture = convertView.findViewById(R.id.ivPicture);
            holder.tvTitle = convertView.findViewById(R.id.tvTitle);
            holder.tvDate = convertView.findViewById(R.id.tvDate);
            //holder.tvToday = convertView.findViewById(R.id.tvToday);
            holder.tvShare = convertView.findViewById(R.id.tvShare);
            holder.ivTwitter = convertView.findViewById(R.id.ivTwitter);
            holder.ivInstagram = convertView.findViewById(R.id.ivInstagram);
            holder.ivVideo = convertView.findViewById(R.id.ivVideo);
            holder.ivDownload = convertView.findViewById(R.id.ivDownload);
            holder.tvImageCounter = convertView.findViewById(R.id.tvImageCounter);

            convertView.setTag(holder);
        } else {
            holder = (ArticleListAdapter.ViewHolder) convertView.getTag();
        }

        if (article.getImages() == null || article.getImages().size() == 0) {
            holder.hsImage.setVisibility(View.GONE);
            holder.ivPicture.setVisibility(View.GONE);
            holder.tvImageCounter.setVisibility(View.GONE);
        } else {
            if (article.getImages().size() == 1) {
                holder.hsImage.setVisibility(View.GONE);
                holder.tvImageCounter.setVisibility(View.GONE);
                holder.ivPicture.setVisibility(View.VISIBLE);
                final String imageUrl = article.getImages().get(0);
                holder.ivPicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mArticleListInterface.onImageClick(article, imageUrl);
                    }
                });
                Glide.with(mContext).load(imageUrl).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(holder.ivPicture);
            } else {
                holder.hsImage.setVisibility(View.VISIBLE);
                holder.loImage.removeAllViews();
                holder.ivPicture.setVisibility(View.GONE);

                int imageMax = 5;
                int imageCount = article.getImages().size();
                if (imageCount > imageMax) imageCount = imageMax;

                // 이미지 로드
                for (int i = 0; i < imageCount; i++) {
                    //Log.e(TAG, "url[" + i + "]: " + imageArray[i]);

                    final String imageUrl = article.getImages().get(i);
                    if (imageUrl.isEmpty()) {
                        continue;
                    }

                    //final ProportionalImageView iv = new ProportionalImageView(mContext);
                    ImageView iv = new ImageView(mContext);
                    if (i == 0) {
                        iv.setLayoutParams(mParamsNoMargin);
                    } else {
                        iv.setLayoutParams(mParams);
                    }
                    iv.setAdjustViewBounds(true);
                    //iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    holder.loImage.addView(iv);
                    iv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mArticleListInterface.onImageClick(article, imageUrl);
                        }
                    });

                    Glide.with(mContext).load(imageUrl).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(iv);
                }

                // 이미지 갯수 출력
                holder.tvImageCounter.setVisibility(View.VISIBLE);
                String countText = (article.getImages().size() > imageMax) ? imageMax + "+" : article.getImages().size() + "";
                holder.tvImageCounter.setText(countText);
            }
        }

        holder.tvTitle.setText(article.getTitle());
        holder.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mArticleListInterface.onTitleClick(article);
            }
        });

        holder.tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mArticleListInterface.onShareClick(article);
            }
        });

        holder.tvDate.setText(article.getDate());

        //if (article.getDate().length() == 5) {
        //    holder.tvToday.setVisibility(View.VISIBLE);
        //} else {
        //    holder.tvToday.setVisibility(View.GONE);
        //}

        if (article.isTwitter()) {
            holder.ivTwitter.setVisibility(View.VISIBLE);
        } else {
            holder.ivTwitter.setVisibility(View.GONE);
        }
        if (article.isInstagram()) {
            holder.ivInstagram.setVisibility(View.VISIBLE);
        } else {
            holder.ivInstagram.setVisibility(View.GONE);
        }
        if (article.isVideo()) {
            holder.ivVideo.setVisibility(View.VISIBLE);
        } else {
            holder.ivVideo.setVisibility(View.GONE);
        }
        if (article.isDownload()) {
            holder.ivDownload.setVisibility(View.VISIBLE);
        } else {
            holder.ivDownload.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class ViewHolder {
        HorizontalScrollView hsImage;
        LinearLayout loImage;
        ImageView ivPicture;
        TextView tvTitle;
        TextView tvDate;
        TextView tvShare;
        //TextView tvToday;
        ImageView ivTwitter;
        ImageView ivInstagram;
        ImageView ivVideo;
        ImageView ivDownload;
        TextView tvImageCounter;
    }
}
