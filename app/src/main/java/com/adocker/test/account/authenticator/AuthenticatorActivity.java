package com.adocker.test.account.authenticator;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adocker.test.R;
import com.adocker.test.account.AccountGeneral;
import com.adocker.test.utils.LogUtil;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Activity which displays login screen to the user.
 */
public class AuthenticatorActivity extends AccountAuthenticatorActivity {
    private static final String TAG = "AuthenticatorActivity";
    private static final String TAG_DIALOG = "dialog";

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";

    public final static String PARAM_USER_PASS = "USER_PASS";

    private final int REQ_SIGNUP = 1;

    private AccountManager mAccountManager;
    private String mAuthTokenType;
    private UserLoginTask mLoginTask;
    private DialogFragment mDialogFragment;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        LogUtil.d(TAG + " onCrate-" + icicle);
        setContentView(R.layout.activity_account_authenticator);

        mAccountManager = AccountManager.get(this);
        final Intent intent = getIntent();
        String accountName = intent.getStringExtra(ARG_ACCOUNT_NAME);
        mAuthTokenType = intent.getStringExtra(ARG_AUTH_TYPE);
        if (mAuthTokenType == null) {
            mAuthTokenType = AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;
        }

        if (accountName != null) {
            ((TextInputLayout) findViewById(R.id.account_name)).getEditText().setText(accountName);
        }

        findViewById(R.id.submit).setOnClickListener(v -> submit());
        findViewById(R.id.signUp).setOnClickListener(v -> {
            // Since there can only be one AuthenticatorActivity, we call the sign up activity, get his results,
            // and return them in setAccountAuthenticatorResult(). See finishLogin().
            Intent signup = new Intent(getBaseContext(), SignUpActivity.class);
            signup.putExtras(getIntent().getExtras());
            startActivityForResult(signup, REQ_SIGNUP);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // The sign up activity returned that the user has successfully created an account
        if (requestCode == REQ_SIGNUP && resultCode == RESULT_OK) {
            finishLogin(data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void submit() {
        final String userName = ((TextInputLayout) findViewById(R.id.account_name)).getEditText().getText().toString();
        final String userPass = ((TextInputLayout) findViewById(R.id.account_password)).getEditText().getText().toString();
        final String accountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);

        mLoginTask = new UserLoginTask();
        mLoginTask.execute(userName, userPass, accountType);
    }

    private void onAuthenticationResult(Intent intent) {
        hideProgress();
        if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
            Toast.makeText(this, intent.getStringExtra(KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
        } else {
            finishLogin(intent);
        }
    }

    private void onAuthenticationCancel() {
        mLoginTask = null;
        hideProgress();
    }

    private void finishLogin(Intent intent) {
        LogUtil.d(TAG + " finishLogin-" + intent);

        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        String accountType = intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
        final Account account = new Account(accountName, accountType);

        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            LogUtil.d(TAG + " finishLogin > addAccountExplicitly");
            String authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authTokenType = mAuthTokenType;

            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            mAccountManager.addAccountExplicitly(account, accountPassword, null);
            mAccountManager.setAuthToken(account, authTokenType, authToken);
        } else {
            LogUtil.d("> finishLogin > setPassword");
            mAccountManager.setPassword(account, accountPassword);
        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    private DialogFragment showDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prevFragment = getFragmentManager().findFragmentByTag(TAG_DIALOG);
        if (prevFragment != null) {
            ft.remove(prevFragment);
        }
        ft.addToBackStack(null);

        // crate and show the dialog
        DialogFragment newFragment = ProgressDialogFragment.newInstance();
        newFragment.show(ft, TAG_DIALOG);
        return newFragment;
    }

    private void hideProgress() {
        if (mDialogFragment != null) {
            mDialogFragment.dismiss();
            mDialogFragment = null;
        }
    }

    public UserLoginTask getLoginTask() {
        return mLoginTask;
    }

    private class UserLoginTask extends AsyncTask<String, Void, Intent> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialogFragment = showDialog();
        }

        @Override
        protected Intent doInBackground(String... strings) {
            LogUtil.d(TAG + " Started authenticating");
            String userName = strings[0];
            String userPass = strings[1];
            String accountType = strings[2];
            LogUtil.i(TAG + " userName:" + userName + ", userPass:" + userPass
                    + ", type:" + accountType);

            String authToken;
            Bundle data = new Bundle();
            try {
                authToken = AccountGeneral.getServerAuthenticate(AuthenticatorActivity.this)
                        .userSignIn(userName, userPass, mAuthTokenType);
                if (TextUtils.isEmpty(authToken)) {
                    data.putString(KEY_ERROR_MESSAGE, "please enter a valid username/password");
                } else {
                    data.putString(AccountManager.KEY_ACCOUNT_NAME, userName);
                    data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                    data.putString(AccountManager.KEY_AUTHTOKEN, authToken);
                    data.putString(PARAM_USER_PASS, userPass);
                }
            } catch (Exception e) {
                data.putString(KEY_ERROR_MESSAGE, e.getMessage());
            }

            final Intent res = new Intent();
            res.putExtras(data);
            return res;
        }

        @Override
        protected void onPostExecute(Intent intent) {
            onAuthenticationResult(intent);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            onAuthenticationCancel();
        }
    }

    public static class ProgressDialogFragment extends DialogFragment {

        public static ProgressDialogFragment newInstance() {
            return new ProgressDialogFragment();
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            final Activity activity = getActivity();
            final ProgressDialog progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Authenticating...");
            progressDialog.setCancelable(true);
            progressDialog.setIndeterminate(true);

            return progressDialog;
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            super.onCancel(dialog);
            LogUtil.d(TAG + " user cancelling authentication");
            AsyncTask asyncTask = ((AuthenticatorActivity) getActivity()).getLoginTask();
            if (asyncTask != null) {
                asyncTask.cancel(true);
            }
        }
    }
}
