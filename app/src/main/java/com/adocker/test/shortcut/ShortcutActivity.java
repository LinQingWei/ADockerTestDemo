package com.adocker.test.shortcut;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;
import androidx.core.content.pm.ShortcutManagerCompat;

import com.adocker.test.R;
import com.adocker.test.base.BaseListAdapter;
import com.adocker.test.base.BaseViewHolder;
import com.adocker.test.utils.LogUtil;
import com.adocker.test.utils.OsUtil;
import com.adocker.test.utils.Utils;

import java.util.List;
import java.util.Set;

public class ShortcutActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ShortcutActivity";
    private static final String ID_ADD_WEBSITE = "add_website";
    private static final String ACTION_ADD_WEBSITE = "com.adocker.test.intent.action.ADD_WEBSITE";

    private ShortcutHelper mShortcutHelper;
    private ShortcutManager mShortcutManager;
    private SimpleAdapter mSimpleAdapter;
    private boolean mEnableShortcut;

    // @GuardedBy("sVisibleInstances")
    private static final Set<ShortcutActivity> sVisibleInstances = new ArraySet<>();

    public static void refreshAllInstances() {
        synchronized (sVisibleInstances) {
            for (ShortcutActivity instance : sVisibleInstances) {
                instance.refreshList();
            }
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, ShortcutActivity.class);
        context.startActivity(intent);
    }

    public static boolean shouldDisableSelf(Context context) {
        final boolean disable = !OsUtil.isAtLeastN_MR1();
        if (disable) {
            Utils.setComponentEnabled(context, new ComponentName(context, ShortcutActivity.class), false);
        }

        return disable;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortcut);
        View add = findViewById(R.id.add);
        View request_pin = findViewById(R.id.request_new_pin_shortcut);
        ListView listView = findViewById(android.R.id.list);

        mShortcutHelper = ShortcutHelper.getInstance(this);
        mShortcutManager = mShortcutHelper.getShortcutManager();
        mEnableShortcut = ShortcutManagerCompat.isRequestPinShortcutSupported(this);

        String action = getIntent().getAction();
        if (!TextUtils.isEmpty(action)) {
            switch (action) {
                case ACTION_ADD_WEBSITE:
                    addWebSite(false, false);
                    break;
                case Intent.ACTION_CREATE_SHORTCUT:
                    addWebSite(true, true);
                    break;
                default:
                    break;
            }
        }

        if (mShortcutManager != null) {
            add.setOnClickListener(this);
            request_pin.setOnClickListener(this);
            mShortcutHelper.maybeRestoreAllDynamicShortcuts();
            mShortcutHelper.refreshShortcuts(false);
            mSimpleAdapter = new SimpleAdapter(null, R.layout.item_shortcut);
            listView.setAdapter(mSimpleAdapter);
        } else {
            add.setEnabled(false);
        }
        request_pin.setEnabled(mEnableShortcut);
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshList();
        synchronized (sVisibleInstances) {
            sVisibleInstances.add(this);
        }
    }

    @Override
    protected void onStop() {
        synchronized (sVisibleInstances) {
            sVisibleInstances.remove(this);
        }
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.add:
                addWebSite(false, false);
                break;
            case R.id.request_new_pin_shortcut:
                addWebSite(true, false);
                break;
            default:
                break;
        }
    }

    private static final int MENU_ITEM_CREATE_PIN = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_ITEM_CREATE_PIN, 0, "create pin");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(MENU_ITEM_CREATE_PIN);
        if (item != null) {
            item.setVisible(mEnableShortcut);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch (id) {
            case MENU_ITEM_CREATE_PIN:
                mShortcutHelper.createShortcutActivityPin();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.N_MR1)
    private void refreshList() {
        mSimpleAdapter.setData(mShortcutHelper.getShortcuts());
    }

    @TargetApi(Build.VERSION_CODES.N_MR1)
    private String getType(ShortcutInfo shortcut) {
        final StringBuilder sb = new StringBuilder();
        String sep = "";
        if (shortcut.isDeclaredInManifest()) {
            sb.append(sep);
            sb.append("Manifest");
            sep = ", ";
        }
        if (shortcut.isDynamic()) {
            sb.append(sep);
            sb.append("Dynamic");
            sep = ", ";
        }
        if (shortcut.isPinned()) {
            sb.append(sep);
            sb.append("Pinned");
            sep = ", ";
        }
        if (!shortcut.isEnabled()) {
            sb.append(sep);
            sb.append("Disabled");
            sep = ", ";
        }
        return sb.toString();
    }

    private void addWebSite(boolean forPin, boolean forResult) {
        LogUtil.i(TAG + " addWebSite forPin=" + forPin);

        // This is important.  This allows the launcher to build a prediction model.
        mShortcutHelper.reportShortcutUsed(ID_ADD_WEBSITE);

        final EditText editUri = new EditText(this);

        editUri.setHint("http://www.android.com/");
        editUri.setInputType(EditorInfo.TYPE_TEXT_VARIATION_URI);

        new AlertDialog.Builder(this)
                .setTitle(forPin ? "Create pin shortcut for website" : "Add new website")
                .setMessage("Type URL of a website")
                .setView(editUri)
                .setPositiveButton("Add", (dialog, whichButton) -> {
                    final String url = editUri.getText().toString().trim();
                    if (url.length() > 0) {
                        addUriAsync(url, forPin, forResult);
                    }
                })
                .setOnCancelListener((dialog) -> {
                    if (forResult) {
                        setResult(Activity.RESULT_CANCELED);
                        finish();
                    }
                })
                .show();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void addUriAsync(final String uri, final boolean forPin, final boolean forResult) {
        if (forResult) {
            new Thread(() -> {
                ShortcutInfo si = mShortcutHelper.createShortcutForUrl(uri);
                runOnUiThread(() -> {
                    setResult(Activity.RESULT_OK,
                            mShortcutManager.createShortcutResultIntent(si));
                    finish();
                });
            }).start();
        } else {
            new Thread(() -> {
                mShortcutHelper.addWebSiteShortcut(uri, forPin);
                runOnUiThread(() -> refreshList());
            }).start();
        }
    }

    @TargetApi(Build.VERSION_CODES.N_MR1)
    private class SimpleAdapter extends BaseListAdapter<ShortcutInfo> {

        public SimpleAdapter(List<ShortcutInfo> data, int itemLayoutId) {
            super(data, itemLayoutId);
        }

        @Override
        protected void convert(int position, ShortcutInfo data, BaseViewHolder holder) {
            ItemClickListener clickListener = new ItemClickListener(data);
            holder.setText(R.id.line1, data.getLongLabel().toString())
                    .setText(R.id.line2, getType(data))
                    .setOnClickListener(R.id.request_pin, clickListener)
                    .setEnable(R.id.request_pin, mEnableShortcut && OsUtil.isAtLeastO())
                    .setOnClickListener(R.id.remove, clickListener)
                    .setEnable(R.id.remove, !data.isImmutable() && data.isDynamic())
                    .setText(R.id.disable, (data.isEnabled() ? "Disable" : "Enable"))
                    .setOnClickListener(R.id.disable, clickListener)
                    .setEnable(R.id.disable, !data.isImmutable());
        }

        private class ItemClickListener implements View.OnClickListener {
            private ShortcutInfo mShortcutInfo;

            public ItemClickListener(ShortcutInfo shortcutInfo) {
                mShortcutInfo = shortcutInfo;
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.disable:
                        if (mShortcutInfo.isEnabled()) {
                            mShortcutHelper.disableShortcut(mShortcutInfo);
                        } else {
                            mShortcutHelper.enableShortcut(mShortcutInfo);
                        }
                        refreshList();
                        break;
                    case R.id.remove:
                        mShortcutHelper.removeShortcut(mShortcutInfo);
                        refreshList();
                        break;
                    case R.id.request_pin:
                        // This is an update case, so just pass the ID.
                        mShortcutHelper.requestPinShortcut(mShortcutInfo.getId());
                        refreshList();
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
