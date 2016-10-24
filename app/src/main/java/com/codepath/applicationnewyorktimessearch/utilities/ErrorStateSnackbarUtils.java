package com.codepath.applicationnewyorktimessearch.utilities;

import com.codepath.applicationnewyorktimessearch.enums.FetchStatus;
import com.codepath.applicationnewyorktimessearch.enums.SearchType;

/**
 * Created by aditikakadebansal on 10/22/16.
 */
public class ErrorStateSnackbarUtils {
    public static String getSnackbarString(FetchStatus status) {
        switch (status) {
            case NO_INTERNET_CONNECTION:
                if (QueryAttributes.getSearchType() == SearchType.INFINITE_SCROLL) {
                    return "Internet connection lost, could not load more articles, retry";
                }
                return "Internet connection lost, retry";
            case NO_RESULTS:
                return "Could not find any results, retry with a different query";
            case REQUEST_FAILURE:
                if (QueryAttributes.getSearchType() == SearchType.INFINITE_SCROLL) {
                    return "Request failed, could not load more articles, retry";
                }
                return "Request failed, retry";
            default:
                throw new IllegalArgumentException();
        }
    }
}
