package com.codepath.applicationnewyorktimessearch.utilities;

import com.codepath.applicationnewyorktimessearch.enums.SearchType;
import com.codepath.applicationnewyorktimessearch.enums.ArticleType;
import com.codepath.applicationnewyorktimessearch.models.ArticleSearchQuery;
import com.loopj.android.http.RequestParams;

/**
 * Created by aditikakadebansal on 10/21/16.
 */
public class QueryAttributes {

    private static final String API_KEY = "4ba7ccef4a8a4db98b2f466817dc1aaa";
    private SearchType mSearchType = SearchType.DEFAULT;
    private int mPage = 0;
    private ArticleType mArticleType = ArticleType.TRENDING;

    private static final String SEARCH_URL = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
    private static final String APP_DEFAULT_URL = "http://api.nytimes.com/svc/mostpopular/v2/mostviewed/all-sections/30.json";

    private static QueryAttributes instance;

    public static void initInstance() {
        if (instance == null) {
            instance = new QueryAttributes();
        }
    }

    public static void clear() {
        instance.setSearchType(SearchType.DEFAULT);
        instance.setPage(0);
    }

    public static SearchType getSearchType() {
        return instance.mSearchType;
    }

    public static void setSearchType(SearchType searchType) {
        instance.mSearchType = searchType;
    }

    public static int getPage() {
        return instance.mPage;
    }

    public static void setPage(int page) {
        instance.mPage = page;
    }

    public static void setArticleType(ArticleType articleType) {
        instance.mArticleType = articleType;
    }

    public static ArticleType getArticleType() {
        return instance.mArticleType;
    }

    public static String getURL() {
        switch (instance.mArticleType) {
            case SEARCH:
                return SEARCH_URL;
            case TRENDING:
                return APP_DEFAULT_URL;
        }
        throw new IllegalArgumentException();
    }

    public static RequestParams getRequestParams() {
        RequestParams params;
        if (QueryAttributes.getArticleType() == ArticleType.SEARCH) {
            params = ArticleSearchQuery.getRequestParams(QueryAttributes.getPage());
        } else {
            params = new RequestParams();
            params.put("offset", QueryAttributes.getPage() * 20);
        }
        params.put("api-key", API_KEY);
        return params;
    }
}
