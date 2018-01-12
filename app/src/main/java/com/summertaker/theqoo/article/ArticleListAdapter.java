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
        int height = (int) (250 * density);
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
            holder.tvTitle = convertView.findViewById(R.id.tvTitle);
            holder.tvDate = convertView.findViewById(R.id.tvDate);
            //holder.tvToday = convertView.findViewById(R.id.tvToday);
            holder.tvShare = convertView.findViewById(R.id.tvShare);
            holder.tvImageCounter = convertView.findViewById(R.id.tvImageCounter);

            convertView.setTag(holder);
        } else {
            holder = (ArticleListAdapter.ViewHolder) convertView.getTag();
        }

        if (article.getImages() == null || article.getImages().size() == 0) {
            holder.hsImage.setVisibility(View.GONE);
            holder.tvImageCounter.setVisibility(View.GONE);
        } else {
            holder.hsImage.setVisibility(View.VISIBLE);
            holder.loImage.removeAllViews();

            // 이미지 로드
            for (int i = 0; i < article.getImages().size(); i++) {
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

                String fileName = Util.getUrlToFileName(imageUrl);
                File file = new File(Config.DATA_PATH, fileName);

                if (file.exists()) {
                    Picasso.with(mContext).load(file).into(iv);
                    //Log.d(mTag, fileName + " local loaded.");
                } else {
                    //Picasso.with(mContext).load(R.drawable.placeholder_320x240).into(iv);
                    Picasso.with(mContext).load(imageUrl).placeholder(R.drawable.placeholder_320x240).into(iv,
                            new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    Log.e(mTag, ">> PICASSO IMAGE LOAD ERROR...");
                                }
                            });

                    Picasso.with(mContext).load(imageUrl).into(getTarget(fileName));
                }
            }

            // 이미지 갯수 출력
            if (article.getImages().size() > 2) {
                holder.tvImageCounter.setVisibility(View.VISIBLE);
                String imageCount = article.getImages().size() + "";
                holder.tvImageCounter.setText(imageCount);
            } else {
                holder.tvImageCounter.setVisibility(View.GONE);
            }
        }

        holder.tvTitle.setText(article.getTitle());
        holder.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mArticleListInterface.onTitleClick(article);
            }
        });

        holder.tvDate.setText(article.getDate());

        //if (article.getDate().length() == 5) {
        //    holder.tvToday.setVisibility(View.VISIBLE);
        //} else {
        //    holder.tvToday.setVisibility(View.GONE);
        //}

        holder.tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mArticleListInterface.onShareClick(article);
            }
        });

        return convertView;
    }

    //target to save
    private Target getTarget(final String fileName) {
        Target target = new Target() {

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        boolean isSuccess;

                        File file = new File(Config.DATA_PATH, fileName);
                        if (file.exists()) {
                            isSuccess = file.delete();
                            //Log.d("==", fileName + " deleted.");
                        }
                        try {
                            isSuccess = file.createNewFile();
                            if (isSuccess) {
                                FileOutputStream ostream = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                                ostream.flush();
                                ostream.close();
                                //Log.d("==", fileName + " created.");
                            } else {
                                Log.e(mTag, fileName + " FAILED.");
                            }
                        } catch (IOException e) {
                            Log.e("IOException", e.getLocalizedMessage());
                        }
                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.e(mTag, ">> PICASSO IMAGE SAVE ERROR...");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        return target;
    }

    static class ViewHolder {
        HorizontalScrollView hsImage;
        LinearLayout loImage;
        TextView tvTitle;
        TextView tvDate;
        TextView tvShare;
        //TextView tvToday;
        TextView tvImageCounter;
    }
}
