package com.codepath.applicationnewyorktimessearch.activities;

import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.codepath.applicationnewyorktimessearch.R;
import com.codepath.applicationnewyorktimessearch.databinding.ActivityArticleBinding;
import com.codepath.applicationnewyorktimessearch.models.Article;

/**
 * Created by aditikakadebansal on 10/18/16.
 */
public class ArticleActivity extends AppCompatActivity {

    private ActivityArticleBinding mActivityArticleBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mActivityArticleBinding = DataBindingUtil.setContentView(this, R.layout.activity_article);

        setSupportActionBar(mActivityArticleBinding.toolbar);

        Article article = getIntent().getParcelableExtra("article");
        mActivityArticleBinding.contentArticleId.textHeadline.setText(article.getHeadline());
        mActivityArticleBinding.contentArticleId.textPublishedDateDetail.setText(article.getPublishedDate());
        mActivityArticleBinding.contentArticleId.textSectionDetail.setText(article.getSection());
    }
}
