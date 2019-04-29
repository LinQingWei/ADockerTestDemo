package com.adocker.test.components.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.widget.Toast;

import com.adocker.test.aidl.IDemoAidl;

public class DemoService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(getApplicationContext(),
                            "DemoService Running " + SystemClock.elapsedRealtime(),
                            Toast.LENGTH_SHORT).show();
                    handler.sendEmptyMessageDelayed(0, 1000);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public class LocalBinder extends IDemoAidl.Stub {

        @Override
        public int sum(int a, int b) {
            return a + b;
        }
    }

    private final IBinder mBinder = new LocalBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "DemoService onStartCommand", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(), "DemoService onBind", Toast.LENGTH_SHORT).show();
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(getApplicationContext(), "DemoService onUnbind", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "DemoService onDestroy", Toast.LENGTH_SHORT).show();
        handler.removeMessages(0);
        super.onDestroy();
    }

    @Override
    public void onRebind(Intent intent) {
        Toast.makeText(getApplicationContext(), "DemoService onRebind", Toast.LENGTH_SHORT).show();
        super.onRebind(intent);
    }
}
