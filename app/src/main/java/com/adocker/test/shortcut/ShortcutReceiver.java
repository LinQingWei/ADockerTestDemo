package com.adocker.test.shortcut;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.adocker.test.utils.LogUtil;
import com.adocker.test.utils.Utils;

public final class ShortcutReceiver extends BroadcastReceiver {
    private static final String TAG = "ShortcutReceiver";
    private static final String ACTION_PIN_REQUEST_ACCEPTED =
            "com.adocker.test.intent.action.PIN_REQUEST_ACCEPTED";

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.i(TAG + " receive:" + intent);
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            LogUtil.w(TAG + " empty action?");
            return;
        }

        switch (action) {
            case Intent.ACTION_LOCALE_CHANGED:
                // Refresh all shortcut to update the labels.
                // (Right now shortcut labels don't contain localized strings though.)
                ShortcutHelper.getInstance(context).refreshShortcuts(true);
                break;
            case ACTION_PIN_REQUEST_ACCEPTED:
                ShortcutActivity.refreshAllInstances();
                Utils.showToast(context, "Pin request accepted");
                break;
            default:
                break;
        }
    }

    public static PendingIntent getPinRequestAcceptedIntent(Context context) {
        final Intent intent = new Intent(ACTION_PIN_REQUEST_ACCEPTED);
        intent.setComponent(new ComponentName(context, ShortcutReceiver.class));

        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
