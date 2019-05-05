package com.adocker.test.account;

import android.content.Context;

import com.adocker.test.account.server.MockServerAuthenticate;
import com.adocker.test.account.server.ServerAuthenticate;

public class AccountGeneral {
    /**
     * Account type
     */
    public static final String ACCOUNT_TYPE = "com.adocker.test_auth";

    /**
     * Auth token types
     */
    public static final String AUTHTOKEN_TYPE_READ_ONLY = "Read only";
    public static final String AUTHTOKEN_TYPE_READ_ONLY_LABEL = "Read only access to an Way account";

    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full access";
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS_LABEL = "Full access to an Way account";

    private static ServerAuthenticate sServerAuthenticate;

    public static ServerAuthenticate getServerAuthenticate(Context context) {
        if (sServerAuthenticate == null) {
            sServerAuthenticate = new MockServerAuthenticate(context);
        }
        return sServerAuthenticate;
    }
}
