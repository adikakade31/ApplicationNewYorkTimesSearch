package com.codepath.applicationnewyorktimessearch.utilities;

import com.codepath.applicationnewyorktimessearch.enums.SortOrder;

/**
 * Created by aditikakadebansal on 10/20/16.
 */

public class SortOrderUtils {
    public static SortOrder getSortOrder(String sortOrder) {
        switch(sortOrder) {
            case "Oldest":
                return SortOrder.OLDEST;
            case "Newest":
                return SortOrder.NEWEST;
        }
        return SortOrder.DEFAULT;
    }

    public static String getSortOrderForRequest(SortOrder sortOrder) {
        switch (sortOrder) {
            case OLDEST:
                return "Oldest";
            case NEWEST:
                return "Newest";
            case DEFAULT:
                return "Default";
        }
        throw new IllegalArgumentException();
    }
}