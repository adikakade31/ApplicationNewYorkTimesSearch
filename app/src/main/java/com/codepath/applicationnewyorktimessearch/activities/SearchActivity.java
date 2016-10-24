package com.codepath.applicationnewyorktimessearch.activities;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;

import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;

import android.widget.TextView;

import com.codepath.applicationnewyorktimessearch.R;
import com.codepath.applicationnewyorktimessearch.adapters.ArticleArrayAdapter;

import com.codepath.applicationnewyorktimessearch.enums.ArticleType;
import com.codepath.applicationnewyorktimessearch.enums.FetchStatus;
import com.codepath.applicationnewyorktimessearch.enums.SearchType;
import com.codepath.applicationnewyorktimessearch.fragments.SettingsDialogFragment;

import com.codepath.applicationnewyorktimessearch.listeners.EndlessRecyclerViewScrollListener;
import com.codepath.applicationnewyorktimessearch.models.Article;
import com.codepath.applicationnewyorktimessearch.models.ArticleSearchQuery;

import com.codepath.applicationnewyorktimessearch.utilities.ErrorStateSnackbarUtils;
import com.codepath.applicationnewyorktimessearch.utilities.InternetConnectivity;
import com.codepath.applicationnewyorktimessearch.utilities.QueryAttributes;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity
        extends AppCompatActivity
        implements SettingsDialogFragment.IDialogSaveListener {

    private RecyclerView mRecyclerViewResults;
    private TextView mTextViewHeader;
    private ArrayList<Article> mArticles;
    private ArticleArrayAdapter mAdapter;
    private FetchStatus mFetchStatus;
    private Snackbar mSnackbar;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setUpData();
        setUpViews();
        renderTrendingView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        MenuItemCompat.setOnActionExpandListener(searchItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem menuItem) {
                        ArticleSearchQuery.clearQuery();
                        mArticles.clear();
                        mAdapter.notifyDataSetChanged();
                        mTextViewHeader.setText("");
                        mTextViewHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        return true;
                    }
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                        renderTrendingView();
                        return true;
                    }
                });

        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchView.clearFocus();
                ArticleSearchQuery.setQuery(query);
                QueryAttributes.setArticleType(ArticleType.SEARCH);
                performSearch();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0) {
                    if (mSnackbar != null) {
                        mSnackbar.dismiss();
                    }
                    mTextViewHeader.setText("");
                    mTextViewHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    ArticleSearchQuery.clearQuery();
                    mArticles.clear();
                    mAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onDialogSave() {
        String queryString = ArticleSearchQuery.getQuery();
        if (queryString.length() == 0) {
            return;
        }
        QueryAttributes.setArticleType(ArticleType.SEARCH);
        performSearch();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            showSettingsDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpData() {
        QueryAttributes.initInstance();
        ArticleSearchQuery.initInstance();
        mArticles = new ArrayList<>();
        mAdapter = new ArticleArrayAdapter(this, mArticles);
    }

    private void setUpViews() {
        mRecyclerViewResults = (RecyclerView) findViewById(R.id.recycler_results);
        mRecyclerViewResults.setHasFixedSize(true);
        mRecyclerViewResults.setAdapter(mAdapter);
        mRecyclerViewResults.setItemAnimator(new DefaultItemAnimator());
        mTextViewHeader = (TextView) findViewById(R.id.text_header);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerViewResults.setLayoutManager(staggeredGridLayoutManager);

        mRecyclerViewResults.addOnScrollListener(new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                QueryAttributes.setPage(page);
                QueryAttributes.setSearchType(SearchType.INFINITE_SCROLL);
                performSearch();
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    private void renderTrendingView() {
        QueryAttributes.setArticleType(ArticleType.TRENDING);
        setViewHeaders();
        performSearch();
    }

    private void showBadStateSnackBar() {
        mSnackbar = Snackbar.make(findViewById(android.R.id.content), ErrorStateSnackbarUtils.getSnackbarString(mFetchStatus), Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", v -> performSearch())
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light));
        ((TextView)mSnackbar.getView().findViewById(android.support.design.R.id.snackbar_text))
                .setTextColor(Color.WHITE);
        mSnackbar.show();
    }

    private void showSettingsDialog() {
        FragmentManager fm = getSupportFragmentManager();
        SettingsDialogFragment settingsDialogFragment = SettingsDialogFragment.newInstance();
        settingsDialogFragment.show(fm, "fragment_layout_settings");
    }

    private void setViewHeaders() {
        if (QueryAttributes.getArticleType() == ArticleType.SEARCH) {
            mTextViewHeader.setText("Showing results for: " + ArticleSearchQuery.getQuery());
            mTextViewHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else {
            mTextViewHeader.setText("TRENDING");
            mTextViewHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_trending, 0);
        }
    }

    private void performSearch() {
        if (mSnackbar != null) {
            mSnackbar.dismiss();
        }
        mFetchStatus = null;
        if (!isInternetConnected()) {
            mFetchStatus = FetchStatus.NO_INTERNET_CONNECTION;
            showBadStateSnackBar();
        }
        setViewHeaders();
        AsyncHttpClient client =  new AsyncHttpClient();
        client.get(QueryAttributes.getURL(), QueryAttributes.getRequestParams(), new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                mFetchStatus = FetchStatus.REQUEST_FAILURE;
                showBadStateSnackBar();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    mAdapter.update(response);
                } catch (JSONException jsonException) {
                    mFetchStatus = FetchStatus.REQUEST_FAILURE;
                    showBadStateSnackBar();
                }
                QueryAttributes.clear();
                if (mArticles.size()==0) {
                    mFetchStatus = FetchStatus.NO_RESULTS;
                    showBadStateSnackBar();
                } else {
                    mFetchStatus = FetchStatus.SUCCESS;
                }
            }
        });
    }

    private Boolean isInternetConnected() {
        return InternetConnectivity.isInternetConnected(
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)
        );
    }
}
