package com.codepath.applicationnewyorktimessearch.utilities;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

/**
 * Created by aditikakadebansal on 10/22/16.
 */
public class InternetConnectivity {
    public static Boolean isInternetConnected(ConnectivityManager connectivityManager) {

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean networkAvailability = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        if (!networkAvailability) {
            return false;
        }
        // Checks if device is connected to internet
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
