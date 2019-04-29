package com.adocker.test.components.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.adocker.test.utils.LogUtil;

public class DemoBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.d("DemoBroadcastReceiver onReceiver, Intent = " + intent);
        Toast.makeText(context, "static broadcast receiver", Toast.LENGTH_SHORT).show();
    }
}
