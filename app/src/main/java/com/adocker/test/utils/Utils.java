package com.adocker.test.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;

public final class Utils {

    private Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void showToast(@NonNull Context context, @NonNull String message) {
        new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        );
    }

    public static void setComponentEnabled(@NonNull Context context, @NonNull ComponentName cpn,
                                           final boolean enable) {
        final PackageManager pm = context.getPackageManager();
        final int enableFlag = enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        pm.setComponentEnabledSetting(cpn, enableFlag, PackageManager.DONT_KILL_APP);
    }
}
