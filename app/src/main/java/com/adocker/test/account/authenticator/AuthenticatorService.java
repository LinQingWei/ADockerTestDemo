package com.adocker.test.account.authenticator;

import android.accounts.AbstractAccountAuthenticator;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.adocker.test.utils.LogUtil;

/**
 * Service to handle Account authentication. It instantiates the authenticator
 * and returns its IBinder.
 */
public class AuthenticatorService extends Service {
    private static final String TAG = "AuthenticatorService";

    private AbstractAccountAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG + " onCreate");
        mAuthenticator = new SimpleAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.d(TAG + " onBind, return for " + intent);
        return mAuthenticator.getIBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG + " onDestroy");
    }
}
