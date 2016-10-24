package com.codepath.applicationnewyorktimessearch.models;

import android.util.Log;

import com.codepath.applicationnewyorktimessearch.enums.NewsDeskType;
import com.codepath.applicationnewyorktimessearch.enums.SortOrder;
import com.codepath.applicationnewyorktimessearch.utilities.NewsDeskTypeUtils;
import com.codepath.applicationnewyorktimessearch.utilities.SortOrderUtils;
import com.loopj.android.http.RequestParams;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;

/**
 * Created by aditikakadebansal on 10/18/16.
 */
public class ArticleSearchQuery {

    private static ArticleSearchQuery instance;
    private String mQueryString = "";
    private String mBeginDate = "";
    private SortOrder mSortOrder = SortOrder.DEFAULT;
    private HashSet<NewsDeskType> mNewsDeskTypes;


    public static void initInstance() {
        if (instance == null) {
            instance = new ArticleSearchQuery();
            instance.mNewsDeskTypes = new HashSet<NewsDeskType>();
        }
    }

    public static void clearQuery() {
        instance.mQueryString = "";
    }

    public static void setQuery(String queryString) {
        instance.mQueryString = queryString;
    }
    public static void setBeginDate(String beginDate) {
        instance.mBeginDate = beginDate;
    }
    public static void setSortOrder(SortOrder sortOrder) {instance.mSortOrder = sortOrder;}
    public static void addToNewsDeskTypes(NewsDeskType newsDeskType) {instance.mNewsDeskTypes.add(newsDeskType);}
    public static void removeFromNewsDeskTypes(NewsDeskType newsDeskType) {
        if (instance.mNewsDeskTypes.contains(newsDeskType)) {
            instance.mNewsDeskTypes.remove(newsDeskType);
        }
    }

    public static String getQuery() {
        return instance.mQueryString;
    }

    public static RequestParams getRequestParams(int page) {
        RequestParams params = new RequestParams();
        params.put("page", page);
        params.put("q", instance.mQueryString);
        if (instance.mSortOrder != SortOrder.DEFAULT) {
            params.put("sort", SortOrderUtils.getSortOrderForRequest(instance.mSortOrder));
        }
        if (instance.mBeginDate.length() != 0) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                params.put("begin_date", new SimpleDateFormat("yyyyMMdd").format(sdf.parse(instance.mBeginDate)));
            } catch (ParseException ex) {
                Log.d("MUSTFIX", ex.getStackTrace().toString());
            }
        }
        if (instance.mNewsDeskTypes.size() > 0) {
            Object[] newsDeskTypesInSet = instance.mNewsDeskTypes.toArray();
            StringBuffer sb = new StringBuffer();
            sb.append("\""+ NewsDeskTypeUtils.getNewsDeskTypeForRequest((NewsDeskType) newsDeskTypesInSet[0])+"\"");
            for (int i=1;i<newsDeskTypesInSet.length;i++) {
                sb.append(" \""+NewsDeskTypeUtils.getNewsDeskTypeForRequest((NewsDeskType) newsDeskTypesInSet[i])+"\"");
            }
            params.put("fq", String.format("news_desk:(%s)", sb.toString()));
        }
        return params;
    }

    public static String getBeginDate() {
        return instance.mBeginDate;
    }

    public static int getSortOrderPosition() {
        switch (instance.mSortOrder) {
            case DEFAULT:
                return 0;
            case OLDEST:
                return 1;
            case NEWEST:
                return 2;
        }
        throw new IllegalArgumentException();
    }

    public static boolean containsNewsDeskType(NewsDeskType newsDeskType) {
        return instance.mNewsDeskTypes.contains(newsDeskType);
    }
}
