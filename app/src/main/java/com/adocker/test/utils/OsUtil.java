package com.adocker.test.utils;

import android.os.Build;

/**
 * Android OS version utilities
 */
public final class OsUtil {
    private static boolean isIsAtLeastJB_MR1;
    private static boolean sIsAtLeastL_MR1;
    private static boolean sIsAtLeastM;
    private static boolean sIsAtLeastN_MR1;
    private static boolean sIsAtLeastO;

    static {
        final int v = getApiVersion();
        isIsAtLeastJB_MR1 = v >= Build.VERSION_CODES.JELLY_BEAN_MR1;
        sIsAtLeastL_MR1 = v >= Build.VERSION_CODES.LOLLIPOP_MR1;
        sIsAtLeastM = v >= Build.VERSION_CODES.M;
        sIsAtLeastN_MR1 = v >= Build.VERSION_CODES.N_MR1;
        sIsAtLeastO = v >= Build.VERSION_CODES.O;
    }

    private OsUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * @return True if the version of Android that we're running on is at least JELLY BEAN MR1
     * (API level 17).
     */
    public static boolean isIsAtLeastJB_MR1() {
        return isIsAtLeastJB_MR1;
    }

    /**
     * @return True if the version of Android that we're running on is at least L MR1
     * (API level 22).
     */
    public static boolean isAtLeastL_MR1() {
        return sIsAtLeastL_MR1;
    }

    /**
     * @return True if the version of Android that we're running on is at least M
     * (API level 23).
     */
    public static boolean isAtLeastM() {
        return sIsAtLeastM;
    }

    /**
     * @return True if the version of Android that we're running on is at least N_MR1
     * (API level 25).
     */
    public static boolean isAtLeastN_MR1() {
        return sIsAtLeastN_MR1;
    }

    /**
     * @return True if the version of Android that we're running on is at least O
     * (API level 26).
     */
    public static boolean isAtLeastO() {
        return sIsAtLeastO;
    }

    /**
     * @return The Android API version of the OS that we're currently running on.
     */
    public static int getApiVersion() {
        return Build.VERSION.SDK_INT;
    }
}
