package com.adocker.test.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class SharedPrefs {
    private static final String SHARED_PREFERENCES_NAME = "ADocker";

    private final Context mContext;
    private String mPrefName;

    public SharedPrefs(Context context) {
        this(context, SHARED_PREFERENCES_NAME);
    }

    public SharedPrefs(Context context, String prefName) {
        mContext = context;
        if (TextUtils.isEmpty(prefName)) {
            mPrefName = SHARED_PREFERENCES_NAME;
        } else {
            mPrefName = prefName;
        }
    }

    /**
     * Returns the shared preferences file name to use.
     * Subclasses should override and return the shared preferences file.
     */
    protected String getSharedPreferencesName() {
        return mPrefName;
    }

    public String getString(final String key, final String defaultValue) {
        final SharedPreferences prefs = mContext.getSharedPreferences(
                getSharedPreferencesName(), Context.MODE_PRIVATE);
        return prefs.getString(key, defaultValue);
    }

    public void putString(final String key, final String value) {
        final SharedPreferences prefs = mContext.getSharedPreferences(
                getSharedPreferencesName(), Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
