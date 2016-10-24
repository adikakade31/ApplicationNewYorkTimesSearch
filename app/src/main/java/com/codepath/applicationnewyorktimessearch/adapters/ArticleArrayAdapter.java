package com.codepath.applicationnewyorktimessearch.adapters;

import android.app.Activity;
import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.applicationnewyorktimessearch.R;
import com.codepath.applicationnewyorktimessearch.activities.ArticleActivity;
import com.codepath.applicationnewyorktimessearch.enums.ArticleType;
import com.codepath.applicationnewyorktimessearch.enums.SearchType;
import com.codepath.applicationnewyorktimessearch.models.Article;
import com.codepath.applicationnewyorktimessearch.utilities.QueryAttributes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class ArticleArrayAdapter extends RecyclerView.Adapter<ArticleArrayAdapter.ArticleBaseViewHolder> {

    private final int VIEWHOLDER_DEFAULT_THUMBNAIL = 1;
    private final int VIEWHOLDER_WITH_THUMBNAIL = 2;

    private List<Article> mArticles;
    private Context mContext;

    public ArticleArrayAdapter(Context context, List<Article> articles) {
        mArticles = articles;
        mContext = context;
    }

    @Override
    public ArticleArrayAdapter.ArticleBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEWHOLDER_DEFAULT_THUMBNAIL:
                return new ArticleViewHolderDefaultThumbnail(
                        inflater.inflate(
                                R.layout.item_article_default_thumbnail,
                                parent,
                                false
                        )
                );
            case VIEWHOLDER_WITH_THUMBNAIL:
                return new ArticleViewHolder(
                        inflater.inflate(
                                R.layout.item_article_result,
                                parent,
                                false
                        )
                );
        }
        throw new IllegalArgumentException();
    }

    @Override
    public void onBindViewHolder(ArticleArrayAdapter.ArticleBaseViewHolder holder, int position) {
        Article article = mArticles.get(position);
        holder.populate(article);
    }

    @Override
    public int getItemViewType(int position) {
        Article article = mArticles.get(position);
        if (article.getThumbnail().length() == 0) return VIEWHOLDER_DEFAULT_THUMBNAIL;
        return VIEWHOLDER_WITH_THUMBNAIL;
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    private void swap(List<Article> articles) {
        mArticles.clear();
        mArticles.addAll(articles);
        this.notifyDataSetChanged();
    }

    private void addNew(List<Article> articles) {
        int curSize = this.getItemCount();
        mArticles.addAll(articles);
        this.notifyItemRangeInserted(curSize, articles.size() - 1);
    }

    public void update(JSONObject response) throws JSONException {
        JSONArray articleJsonResults = null;
        if (QueryAttributes.getArticleType() == ArticleType.TRENDING) {
            articleJsonResults = response.getJSONArray("results");
        } else {
            articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
        }
        if (QueryAttributes.getSearchType() == SearchType.INFINITE_SCROLL) {
            this.addNew(Article.fromJSONArray(articleJsonResults, QueryAttributes.getArticleType()));
        } else {
            this.swap(Article.fromJSONArray(articleJsonResults, QueryAttributes.getArticleType()));
        }
    }

    abstract class ArticleBaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        abstract public void populate(Article article);

        public ArticleBaseViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Article article = mArticles.get(position);

            Intent showArticleDetailsIntent = new Intent(mContext, ArticleActivity.class);
            showArticleDetailsIntent.putExtra("article", article);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    mContext,
                    1,
                    showArticleDetailsIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setToolbarColor(Color.DKGRAY);
            builder.addDefaultShareMenuItem();
            builder.addMenuItem("Show Article Details [DEBUG]", pendingIntent);

            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl((Activity) mContext, Uri.parse(article.getWebUrl()));
        }
    }

    class ArticleViewHolder extends ArticleBaseViewHolder {

        ImageView imageView;
        TextView textView;
        public ArticleViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_thumbnail);
            textView = (TextView) itemView.findViewById(R.id.text_headline);
        }

        public void populate(Article article) {
            textView.setText(article.getHeadline());
            imageView.setImageResource(0);
            String thumbnail = article.getThumbnail();
            if (!TextUtils.isEmpty(thumbnail)) {
                Glide.with(mContext).load(thumbnail).into(imageView);
            }
        }
    }

    class ArticleViewHolderDefaultThumbnail extends ArticleBaseViewHolder {

        TextView textView;
        public ArticleViewHolderDefaultThumbnail(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text_headline);
        }

        public void populate(Article article) {
            textView.setText(article.getHeadline());
        }
    }
}
