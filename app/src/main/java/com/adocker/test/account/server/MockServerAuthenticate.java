package com.adocker.test.account.server;

import android.content.Context;
import android.text.TextUtils;

import com.adocker.test.utils.LogUtil;
import com.adocker.test.utils.SharedPrefs;

public class MockServerAuthenticate implements ServerAuthenticate {
    private static final String TAG = "MockServerAuthenticate";
    private static final String MOCK_SERVER_PREFS = "mock_server";
    private static final String SPLIT_KEY = "&";

    private final SharedPrefs mSharedPrefs;

    public MockServerAuthenticate(Context context) {
        mSharedPrefs = new SharedPrefs(context.getApplicationContext(), MOCK_SERVER_PREFS);
    }

    @Override
    public String userSignUp(String name, String email, String pass, String authType) {
        String authToken = generateAuthToken(email, pass);
        mSharedPrefs.putString(authToken, name);
        LogUtil.d(TAG + " authToken:" + authToken + ", name:" + name);

        return authToken;
    }

    @Override
    public String userSignIn(String user, String pass, String authType) {
        String authToken = generateAuthToken(user, pass);
        final boolean exist = !TextUtils.isEmpty(mSharedPrefs.getString(authToken, ""));
        LogUtil.d(TAG + " authToken:" + authToken + ", exist:" + exist);

        if (exist) {
            return authToken;
        }
        return null;
    }

    private String generateAuthToken(String email, String pass) {
        return email + SPLIT_KEY + pass;
    }
}
