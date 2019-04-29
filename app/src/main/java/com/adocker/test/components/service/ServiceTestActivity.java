package com.adocker.test.components.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.adocker.test.R;

import androidx.appcompat.app.AppCompatActivity;

public class ServiceTestActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mStartServiceBtn;
    private Button mStopServiceBtn;
    private Button mBindServiceBtn;
    private Button mUnBindServiceBtn;

    private boolean mIsBindService;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIsBindService = true;
            DemoService.LocalBinder mBoundService = (DemoService.LocalBinder) service;
            Toast.makeText(ServiceTestActivity.this, "Local Service Connected = " + mBoundService.sum(1, 1),
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(ServiceTestActivity.this, "Local Service Disconnected = " + name.toShortString(),
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBindingDied(ComponentName name) {
            Toast.makeText(ServiceTestActivity.this, "Local Service Died = " + name.toShortString(),
                    Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_test);
        initView();
    }

    private void initView() {
        mStartServiceBtn = (Button) findViewById(R.id.btn_start_service);
        mStartServiceBtn.setOnClickListener(this);
        mStopServiceBtn = (Button) findViewById(R.id.btn_stop_service);
        mStopServiceBtn.setOnClickListener(this);
        mBindServiceBtn = (Button) findViewById(R.id.btn_bind_service);
        mBindServiceBtn.setOnClickListener(this);
        mUnBindServiceBtn = (Button) findViewById(R.id.btn_unbind_service);
        mUnBindServiceBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_start_service:
                startService(new Intent(ServiceTestActivity.this, DemoService.class));
                break;
            case R.id.btn_stop_service:
                stopService(new Intent(ServiceTestActivity.this, DemoService.class));
                break;
            case R.id.btn_bind_service:
                bindService(new Intent(ServiceTestActivity.this, DemoService.class),
                        mServiceConnection, Context.BIND_AUTO_CREATE);
                break;
            case R.id.btn_unbind_service:
                if (mIsBindService) {
                    unbindService(mServiceConnection);
                    mIsBindService = false;
                }
                break;
            default:
                break;
        }
    }
}
