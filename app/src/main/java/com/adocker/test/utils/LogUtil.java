package com.adocker.test.utils;

import android.util.Log;

public class LogUtil {

    private static final String TAG = "ADocker-Test";

    public static void d(String string) {
        Log.d(TAG, string);
    }

    public static void i(String string) {
        Log.i(TAG, string);
    }

    public static void w(String string) {
        Log.w(TAG, string);
    }

    public static void e(String string) {
        Log.e(TAG, string);
    }

    public static void e(String string, Throwable tr) {
        Log.e(TAG, string, tr);
    }
}
