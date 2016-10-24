package com.codepath.applicationnewyorktimessearch.utilities;

import com.codepath.applicationnewyorktimessearch.R;
import com.codepath.applicationnewyorktimessearch.enums.NewsDeskType;

/**
 * Created by aditikakadebansal on 10/20/16.
 */
public class NewsDeskTypeUtils {
    public static int getResourceID(NewsDeskType newsDeskType) {
        switch(newsDeskType) {
            case ARTS:
                return R.id.checkbox_arts;
            case FASHION_STYLE:
                return R.id.checkbox_fashion;
            case SPORTS:
                return R.id.checkbox_sports;
        }
        throw new IllegalArgumentException();
    }

    public static String getNewsDeskTypeForRequest(NewsDeskType newsDeskType) {
        switch(newsDeskType) {
            case ARTS:
                return "Arts";
            case FASHION_STYLE:
                return "Fashion & Style";
            case SPORTS:
                return "Sports";
        }
        throw new IllegalArgumentException();
    }
}