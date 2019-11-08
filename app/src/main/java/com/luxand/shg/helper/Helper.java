package com.luxand.shg.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Helper {
    private static Helper instance = null;
    public static String DATFILE = "/sdcard/SHG/DATFILES";
    public static String LOGIN_DATFILE = "/sdcard/SHG";
    public static String PROFILE_IMAGE = "/sdcard/SHG";
    public static String IMAGES = "/sdcard/SHG/IMAGES";
    public static final String GROUP_DATFILE = "GROUP";
    public static final String STAFF_DATFILE = "STAFF";
    public static final String GROUP_IMAGES = "GROUP";
    public static final String STAFF_IMAGES = "STAFF";


    public static boolean isNetWorkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}
