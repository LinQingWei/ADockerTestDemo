package com.adocker.test.shortcut;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PersistableBundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.adocker.test.R;
import com.adocker.test.utils.BitmapUtils;
import com.adocker.test.utils.LogUtil;
import com.adocker.test.utils.OsUtil;
import com.adocker.test.utils.Utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.BooleanSupplier;

public final class ShortcutHelper {
    private static final String TAG = "ShortcutHelper";
    private static final String ACTION_INSTALL_SHORTCUT =
            "com.android.launcher.action.INSTALL_SHORTCUT";
    private static final String EXTRA_LAST_REFRESH =
            "cn.way.demo.shortcut.EXTRA_LAST_REFRESH";
    private static final long REFRESH_INTERVAL_MS = 60 * 60 * 1000L;
    private volatile static ShortcutHelper sInstance;

    private final Context mContext;
    @Nullable
    @TargetApi(Build.VERSION_CODES.N_MR1)
    private final ShortcutManager mShortcutManager;

    private ShortcutHelper(Context context) {
        mContext = context.getApplicationContext();
        if (OsUtil.isAtLeastN_MR1()) {
            mShortcutManager = (ShortcutManager) mContext.getSystemService(Context.SHORTCUT_SERVICE);
        } else {
            mShortcutManager = null;
        }
    }

    public static ShortcutHelper getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (ShortcutHelper.class) {
                if (sInstance == null) {
                    sInstance = new ShortcutHelper(context);
                }
            }
        }

        return sInstance;
    }

    @Nullable
    public ShortcutManager getShortcutManager() {
        return mShortcutManager;
    }

    @TargetApi(Build.VERSION_CODES.N_MR1)
    public void maybeRestoreAllDynamicShortcuts() {
        if (mShortcutManager != null && mShortcutManager.getDynamicShortcuts().size() == 0) {
            // NOTE: If this application is always supposed to have dynamic shortcuts, then publish
            // them here.
            // Note when an application is "restored" on a new device, all dynamic shortcuts
            // will *not* be restored but the pinned shortcuts *will*.
        }
    }

    @TargetApi(Build.VERSION_CODES.N_MR1)
    public void reportShortcutUsed(String id) {
        if (mShortcutManager != null) {
            mShortcutManager.reportShortcutUsed(id);
        }
    }

    /**
     * Return all shortcuts from this ic_app self.
     */
    @TargetApi(Build.VERSION_CODES.N_MR1)
    public List<ShortcutInfo> getShortcuts() {
        final List<ShortcutInfo> ret = new ArrayList<>();
        if (mShortcutManager == null) {
            return ret;
        }
        final HashSet<String> seenKeys = new HashSet<>();

        for (ShortcutInfo shortcut : mShortcutManager.getManifestShortcuts()) {
            ret.add(shortcut);
            seenKeys.add(shortcut.getId());
        }
        for (ShortcutInfo shortcut : mShortcutManager.getDynamicShortcuts()) {
            if (!seenKeys.contains(shortcut.getId())) {
                ret.add(shortcut);
                seenKeys.add(shortcut.getId());
            }
        }
        for (ShortcutInfo shortcut : mShortcutManager.getPinnedShortcuts()) {
            if (!seenKeys.contains(shortcut.getId())) {
                ret.add(shortcut);
                seenKeys.add(shortcut.getId());
            }
        }
        return ret;
    }

    /**
     * Called when the activity starts.  Looks for shortcuts that have been pushed and refreshes
     * them (but the refresh part isn't implemented yet...).
     */
    @TargetApi(Build.VERSION_CODES.N_MR1)
    public void refreshShortcuts(final boolean force) {
        new RefreshShortcutTask().equals(force);
    }

    @TargetApi(Build.VERSION_CODES.N_MR1)
    public ShortcutInfo createShortcutForUrl(String urlAsString) {
        LogUtil.i(TAG + " createShortcutForUrl: " + urlAsString);
        urlAsString = normalizeUrl(urlAsString);

        final ShortcutInfo.Builder b = new ShortcutInfo.Builder(mContext, urlAsString);

        final Uri uri = Uri.parse(urlAsString);
        b.setIntent(new Intent(Intent.ACTION_VIEW, uri));

        setSiteInformation(b, uri);
        setExtras(b);

        return b.build();
    }

    /** before N_MR1 */
    public Intent createShortcutIntent(String urlAsString) {
        final Uri uri = Uri.parse(urlAsString);
        Intent shortcutIntent = new Intent(Intent.ACTION_VIEW, uri);

        String host = uri.getHost();
        String label = TextUtils.isEmpty(host) ? urlAsString : host;

        Bitmap bmp = fetchFavicon(uri);
        if (bmp == null) {
            bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.link);
        }

        Intent intent = new Intent(ACTION_INSTALL_SHORTCUT);
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, label);
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapUtils.wrapperIcon(bmp, 256, 256));
        intent.putExtra("duplicate", false);

        return intent;
    }

    /** before N_MR1 */
    public boolean requestPinShortcut(Intent shortcutIntent, IntentSender callback) {
        if (!ShortcutManagerCompat.isRequestPinShortcutSupported(mContext)) {
            return false;
        }

        // If the callback is null, just send the broadcast
        if (callback == null) {
            mContext.sendBroadcast(shortcutIntent);
            return true;
        }

        // Otherwise send the callback when the intent has successfully been dispatched.
        mContext.sendOrderedBroadcast(shortcutIntent, null, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    callback.sendIntent(context, 0, null, null, null);
                } catch (IntentSender.SendIntentException e) {
                    // Ignore
                }
            }
        }, null, Activity.RESULT_OK, null, null);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void addWebSiteShortcut(String urlAsString, boolean forPin) {
        if (forPin && !OsUtil.isAtLeastO()) {
            Intent intent = createShortcutIntent(urlAsString);
            requestPinShortcut(intent, ShortcutReceiver.getPinRequestAcceptedIntent(mContext).getIntentSender());
            return;
        }
        final ShortcutInfo shortcut = createShortcutForUrl(urlAsString);

        if (forPin) {
            callShortcutManager(() -> mShortcutManager.requestPinShortcut(
                    shortcut, ShortcutReceiver.getPinRequestAcceptedIntent(mContext).getIntentSender()));
        } else {
            callShortcutManager(() ->
                    mShortcutManager.addDynamicShortcuts(Arrays.asList(shortcut)));
        }
    }

    @TargetApi(Build.VERSION_CODES.N_MR1)
    public void removeShortcut(ShortcutInfo shortcut) {
        mShortcutManager.removeDynamicShortcuts(Arrays.asList(shortcut.getId()));
    }

    @TargetApi(Build.VERSION_CODES.N_MR1)
    public void disableShortcut(ShortcutInfo shortcut) {
        mShortcutManager.disableShortcuts(Arrays.asList(shortcut.getId()));
    }

    @TargetApi(Build.VERSION_CODES.N_MR1)
    public void enableShortcut(ShortcutInfo shortcut) {
        mShortcutManager.enableShortcuts(Arrays.asList(shortcut.getId()));
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void requestPinShortcut(String id) {
        mShortcutManager.requestPinShortcut(
                new ShortcutInfo.Builder(mContext, id).build(),
                ShortcutReceiver.getPinRequestAcceptedIntent(mContext).getIntentSender());
    }

    public void createShortcutActivityPin() {
        Intent intent = new Intent(mContext, ShortcutActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        ShortcutInfoCompat.Builder builder = new ShortcutInfoCompat.Builder(mContext, "self");
        builder.setIntent(intent)
                .setIcon(IconCompat.createWithResource(mContext, R.drawable.app))
                .setShortLabel(mContext.getString(R.string.app_name))
                .setLongLabel(mContext.getString(R.string.shortcut));
        ShortcutManagerCompat.requestPinShortcut(mContext, builder.build(),
                ShortcutReceiver.getPinRequestAcceptedIntent(mContext).getIntentSender());
    }

    /**
     * Use this when interacting with ShortcutManager to show consistent error messages.
     */
    @TargetApi(Build.VERSION_CODES.N_MR1)
    private void callShortcutManager(BooleanSupplier r) {
        try {
            if (!r.getAsBoolean()) {
                Utils.showToast(mContext, "Call to ShortcutManager is rate-limited");
            }
        } catch (Exception e) {
            LogUtil.e(TAG + " Caught Exception", e);
            Utils.showToast(mContext, "Error while calling ShortcutManager: " + e.toString());
        }
    }

    @TargetApi(Build.VERSION_CODES.N_MR1)
    private ShortcutInfo.Builder setSiteInformation(ShortcutInfo.Builder b, Uri uri) {
        // TODO Get the actual site <title> and use it.
        // TODO Set the current locale to accept-language to get localized title.
        b.setShortLabel(uri.getHost());
        b.setLongLabel(uri.toString());

        Bitmap bmp = fetchFavicon(uri);
        if (bmp != null) {
            b.setIcon(Icon.createWithBitmap(bmp));
        } else {
            b.setIcon(Icon.createWithResource(mContext, R.drawable.link));
        }

        return b;
    }

    @TargetApi(Build.VERSION_CODES.N_MR1)
    private ShortcutInfo.Builder setExtras(ShortcutInfo.Builder b) {
        final PersistableBundle extras = new PersistableBundle();
        extras.putLong(EXTRA_LAST_REFRESH, System.currentTimeMillis());
        b.setExtras(extras);
        return b;
    }

    private Bitmap fetchFavicon(Uri uri) {
        final Uri iconUri = uri.buildUpon().path("favicon.ico").build();
        LogUtil.i(TAG + " Fetching favicon from: " + iconUri);

        InputStream is;
        BufferedInputStream bis;
        try {
            URLConnection conn = new URL(iconUri.toString()).openConnection();
            conn.connect();
            is = conn.getInputStream();
            bis = new BufferedInputStream(is, 8192);
            return BitmapFactory.decodeStream(bis);
        } catch (IOException e) {
            LogUtil.e(TAG + " Failed to fetch favicon from " + iconUri, e);
            return null;
        }
    }

    private String normalizeUrl(String urlAsString) {
        if (urlAsString.startsWith("http://") || urlAsString.startsWith("https://")) {
            return urlAsString;
        } else {
            return "http://" + urlAsString;
        }
    }

    @TargetApi(Build.VERSION_CODES.N_MR1)
    private class RefreshShortcutTask extends AsyncTask<Boolean, Void, Void> {

        @Override
        protected Void doInBackground(Boolean... booleans) {
            final boolean force = booleans[0];
            LogUtil.i(TAG + " refreshing shortcuts..." + force);

            final long now = System.currentTimeMillis();
            final long staleThreshold = force ? now : now - REFRESH_INTERVAL_MS;

            // Check all existing dynamic and pinned shortcut, and if their last refresh
            // time is older than a certain threshold, update them.

            final List<ShortcutInfo> updateList = new ArrayList<>();

            for (ShortcutInfo shortcut : getShortcuts()) {
                if (shortcut.isImmutable()) {
                    continue;
                }

                final PersistableBundle extras = shortcut.getExtras();
                if (extras != null && extras.getLong(EXTRA_LAST_REFRESH) >= staleThreshold) {
                    // Shortcut still fresh.
                    continue;
                }
                LogUtil.i(TAG + " Refreshing shortcut: " + shortcut.getId());

                final ShortcutInfo.Builder b = new ShortcutInfo.Builder(
                        mContext, shortcut.getId());

                setSiteInformation(b, shortcut.getIntent().getData());
                setExtras(b);

                updateList.add(b.build());
            }
            // Call update.
            if (updateList.size() > 0) {
                callShortcutManager(() -> mShortcutManager.updateShortcuts(updateList));
            }

            return null;
        }
    }
}
