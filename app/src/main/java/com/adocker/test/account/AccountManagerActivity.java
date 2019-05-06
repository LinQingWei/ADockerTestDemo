package com.adocker.test.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.adocker.test.R;
import com.adocker.test.utils.LogUtil;

import static com.adocker.test.account.AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;

public class AccountManagerActivity extends AppCompatActivity {
    private static final String TAG = "AccountManagerActivity";

    private static final String STATE_DIALOG = "state_dialog";
    private static final String STATE_INVALIDATE = "state_invalidate";

    private AccountManager mAccountManager;
    private AlertDialog mAlertDialog;
    private boolean mInvalidate;

    public static void start(Context context) {
        Intent intent = new Intent(context, AccountManagerActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manager);

        init(savedInstanceState);
    }

    protected void init(@Nullable Bundle savedInstanceState) {
        mAccountManager = AccountManager.get(this);

        findViewById(R.id.btn_add_account).setOnClickListener(v ->
                addNewAccount(AccountGeneral.ACCOUNT_TYPE, AUTHTOKEN_TYPE_FULL_ACCESS));

        findViewById(R.id.btn_get_authToken).setOnClickListener(v ->
                showAccountPicker(AUTHTOKEN_TYPE_FULL_ACCESS, false));

        findViewById(R.id.btn_get_auth_token_convenient).setOnClickListener(v ->
                getTokenForAccountCreateIfNeeded(AccountGeneral.ACCOUNT_TYPE, AUTHTOKEN_TYPE_FULL_ACCESS));

        findViewById(R.id.btn_invalidate_auth_token).setOnClickListener(v ->
                showAccountPicker(AUTHTOKEN_TYPE_FULL_ACCESS, true));

        if (savedInstanceState != null) {
            boolean showDialog = savedInstanceState.getBoolean(STATE_DIALOG);
            if (showDialog) {
                boolean invalidate = savedInstanceState.getBoolean(STATE_INVALIDATE);
                showAccountPicker(AUTHTOKEN_TYPE_FULL_ACCESS, invalidate);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            outState.putBoolean(STATE_DIALOG, true);
            outState.putBoolean(STATE_INVALIDATE, mInvalidate);
        }
    }

    /**
     * Add new account to the account manager
     * @param accountType
     * @param authTokenType
     */
    private void addNewAccount(String accountType, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.addAccount(
                accountType, authTokenType, null, null, this,
                future1 -> {
                    try {
                        Bundle bnd = future1.getResult();
                        showMessage("Account was created");
                        LogUtil.d(TAG + " AddNewAccount Bundle is " + bnd);
                    } catch (Exception e) {
                        e.printStackTrace();
                        showMessage(e.getMessage());
                    }
                }, null);
    }

    /**
     * Show all the accounts registered on the account manager. Request an auth token upon user select.
     * @param authTokenType
     */
    private void showAccountPicker(final String authTokenType, final boolean invalidate) {
        mInvalidate = invalidate;
        final Account availableAccounts[] = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

        if (availableAccounts.length == 0) {
            Toast.makeText(this, "No accounts", Toast.LENGTH_SHORT).show();
        } else {
            String name[] = new String[availableAccounts.length];
            for (int i = 0; i < availableAccounts.length; i++) {
                name[i] = availableAccounts[i].name;
            }

            // Account picker
            mAlertDialog = new AlertDialog.Builder(this)
                    .setTitle("Pick Account")
                    .setAdapter(new ArrayAdapter<>(getBaseContext(),
                                    android.R.layout.simple_list_item_1, name),
                            (dialog, which) -> {
                                if (invalidate) {
                                    invalidateAuthToken(availableAccounts[which], authTokenType);
                                } else {
                                    getExistingAccountAuthToken(availableAccounts[which], authTokenType);
                                }
                            })
                    .create();
            mAlertDialog.show();
        }
    }

    /**
     * Get the auth token for an existing account on the AccountManager
     * @param account
     * @param authTokenType
     */
    private void getExistingAccountAuthToken(Account account, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(account,
                authTokenType, null, this, null, null);

        new Thread(() -> {
            try {
                Bundle bnd = future.getResult();

                final String authToken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                showMessage((authToken != null) ? "SUCCESS!\ntoken: " + authToken : "FAIL");
                LogUtil.d(TAG + " GetToken Bundle is " + bnd);
            } catch (Exception e) {
                e.printStackTrace();
                showMessage(e.getMessage());
            }
        }).start();
    }

    /**
     * Invalidates the auth token for the account
     * @param account
     * @param authTokenType
     */
    private void invalidateAuthToken(final Account account, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(account,
                authTokenType, null, this, null, null);

        new Thread(() -> {
            try {
                Bundle bnd = future.getResult();
                final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                mAccountManager.invalidateAuthToken(account.type, authtoken);
                showMessage(account.name + " invalidated");
            } catch (Exception e) {
                e.printStackTrace();
                showMessage(e.getMessage());
            }
        }).start();
    }

    /**
     * Get an auth token for the account.
     * If not exist - add it and then return its auth token.
     * If one exist - return its auth token.
     * If more than one exists - show a picker and return the select account's auth token.
     * @param accountType
     * @param authTokenType
     */
    private void getTokenForAccountCreateIfNeeded(String accountType, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthTokenByFeatures(
                accountType, authTokenType, null, this, null, null,
                future1 -> {
                    Bundle bnd;
                    try {
                        bnd = future1.getResult();
                        final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                        showMessage(((authtoken != null) ? "SUCCESS!\ntoken: " + authtoken : "FAIL"));
                        LogUtil.d(TAG + " GetTokenForAccount Bundle is " + bnd);
                    } catch (Exception e) {
                        e.printStackTrace();
                        showMessage(e.getMessage());
                    }
                }, null);
    }

    private void showMessage(final String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }

        runOnUiThread(() -> Toast.makeText(AccountManagerActivity.this, msg,
                Toast.LENGTH_SHORT).show());
    }
}
