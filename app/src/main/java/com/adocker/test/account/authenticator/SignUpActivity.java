package com.adocker.test.account.authenticator;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.adocker.test.R;
import com.adocker.test.account.AccountGeneral;
import com.adocker.test.utils.LogUtil;
import com.google.android.material.textfield.TextInputLayout;

import static com.adocker.test.account.authenticator.AuthenticatorActivity.ARG_ACCOUNT_TYPE;
import static com.adocker.test.account.authenticator.AuthenticatorActivity.KEY_ERROR_MESSAGE;
import static com.adocker.test.account.authenticator.AuthenticatorActivity.PARAM_USER_PASS;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";

    private String mAccountType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_signup);

        init(savedInstanceState);
    }

    protected void init(@Nullable Bundle savedInstanceState) {
        mAccountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);
        findViewById(R.id.already_member).setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
        findViewById(R.id.submit).setOnClickListener(v -> createAccount());
    }

    private void createAccount() {
        // Validation!
        String name = ((TextInputLayout) findViewById(R.id.name)).getEditText()
                .getText().toString().trim();
        String accountName = ((TextInputLayout) findViewById(R.id.account_name))
                .getEditText().getText().toString().trim();
        String accountPassword = ((TextInputLayout) findViewById(R.id.account_password))
                .getEditText().getText().toString().trim();
        new CreateAccountTask().execute(name, accountName, accountPassword);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    private class CreateAccountTask extends AsyncTask<String, Void, Intent> {

        @Override
        protected Intent doInBackground(String... strings) {
            String name = strings[0];
            String accountName = strings[1];
            String accountPassword = strings[2];
            LogUtil.d(TAG + " Started authenticating, name:" + name
                    + ", accountName:" + accountName + ", password:" + accountPassword);

            String authToken;
            Bundle data = new Bundle();
            try {
                authToken = AccountGeneral.getServerAuthenticate(SignUpActivity.this)
                        .userSignUp(name, accountName, accountPassword,
                                AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);

                data.putString(AccountManager.KEY_ACCOUNT_NAME, accountName);
                data.putString(AccountManager.KEY_ACCOUNT_TYPE, mAccountType);
                data.putString(AccountManager.KEY_AUTHTOKEN, authToken);
                data.putString(PARAM_USER_PASS, accountPassword);
            } catch (Exception e) {
                data.putString(KEY_ERROR_MESSAGE, e.getMessage());
            }

            final Intent res = new Intent();
            res.putExtras(data);
            return res;
        }

        @Override
        protected void onPostExecute(Intent intent) {
            if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
                Toast.makeText(SignUpActivity.this, intent.getStringExtra(KEY_ERROR_MESSAGE),
                        Toast.LENGTH_SHORT).show();
            } else {
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }
}
