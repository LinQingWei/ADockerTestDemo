package com.adocker.test;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.adocker.test.account.AccountManagerActivity;
import com.adocker.test.components.activity.DemoActivity;
import com.adocker.test.components.contentprovider.DemoContentProvider;
import com.adocker.test.components.dialog.AlertDialogSamples;
import com.adocker.test.components.job.DemoJobService;
import com.adocker.test.components.notification.NotificationTestActivity;
import com.adocker.test.components.service.ServiceTestActivity;
import com.adocker.test.shortcut.ShortcutActivity;
import com.adocker.test.utils.LogUtil;

import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mActivityTestBtn;
    private Button mResolverActivityTestBtn;
    private Button mServiceTestBtn;
    private Button mStaticBRTestBtn;
    private Button mDynamicBRTestBtn;
    private Button mCRTestBtn;
    private Button mDialogTestBtn;
    private Button mNFTestBtn;
    private Button mJobServiceTestBtn;
    private Button mAccountManageTestBtn;
    private Button mShortcutTestBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        mActivityTestBtn = (Button) findViewById(R.id.btn_activity_test);
        mActivityTestBtn.setOnClickListener(this);
        mResolverActivityTestBtn = (Button) findViewById(R.id.btn_resolver_activity_test);
        mResolverActivityTestBtn.setOnClickListener(this);
        mServiceTestBtn = (Button) findViewById(R.id.btn_service_test);
        mServiceTestBtn.setOnClickListener(this);
        mStaticBRTestBtn = (Button) findViewById(R.id.btn_static_broadcast_test);
        mStaticBRTestBtn.setOnClickListener(this);
        mDynamicBRTestBtn = (Button) findViewById(R.id.btn_dynamic_broadcast_test);
        mDynamicBRTestBtn.setOnClickListener(this);
        mCRTestBtn = (Button) findViewById(R.id.btn_content_provider_test);
        mCRTestBtn.setOnClickListener(this);
        mDialogTestBtn = (Button) findViewById(R.id.btn_dialog_test);
        mDialogTestBtn.setOnClickListener(this);
        mNFTestBtn = (Button) findViewById(R.id.btn_notification_test);
        mNFTestBtn.setOnClickListener(this);
        mJobServiceTestBtn = (Button) findViewById(R.id.btn_job_test);
        mJobServiceTestBtn.setOnClickListener(this);
        mAccountManageTestBtn = findViewById(R.id.btn_account_manage);
        mAccountManageTestBtn.setOnClickListener(this);
        mShortcutTestBtn = findViewById(R.id.btn_shortcut);
        if (ShortcutActivity.shouldDisableSelf(this)) {
            mShortcutTestBtn.setEnabled(false);
        } else {
            mShortcutTestBtn.setOnClickListener(this);
        }

        //static broadcast test
        IntentFilter intentFilter = new IntentFilter("com.adocker.test.DYNAMIC_RECEIVER");
        registerReceiver(mDynamicReceiver, intentFilter);

        //*/ Permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_activity_test:
                startActivity(new Intent(MainActivity.this, DemoActivity.class));
                break;
            case R.id.btn_resolver_activity_test:
                startActivity(new Intent("com.adocker.test.ACTION_START"));
                break;
            case R.id.btn_service_test:
                startActivity(new Intent(MainActivity.this, ServiceTestActivity.class));
                break;
            case R.id.btn_static_broadcast_test:
                Intent staticIntent = new Intent("com.adocker.test.STATIC_RECEIVER");
                staticIntent.setPackage(getPackageName());
                sendBroadcast(staticIntent);
                break;
            case R.id.btn_dynamic_broadcast_test:
                Intent dynamicIntent = new Intent("com.adocker.test.DYNAMIC_RECEIVER");
                dynamicIntent.setPackage(getPackageName());
                sendBroadcast(dynamicIntent);
                break;
            case R.id.btn_content_provider_test:
                ContentResolver resolver = getContentResolver();
                Cursor cursor = resolver.query(DemoContentProvider.getDemoUri(MainActivity.this),
                        null, null, null, null);
                StringBuilder sb = new StringBuilder();
                cursor.moveToFirst();
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    sb.append(name);
                    sb.append(',');
                }
                LogUtil.d("ContentProvider Test : " + sb.toString());
                Toast.makeText(MainActivity.this, sb.toString(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_dialog_test:
                startActivity(new Intent(MainActivity.this, AlertDialogSamples.class));
                break;
            case R.id.btn_notification_test:
                startActivity(new Intent(MainActivity.this, NotificationTestActivity.class));
                break;
            case R.id.btn_job_test:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startJobServiceTest();
                }
                break;
            case R.id.btn_account_manage:
                AccountManagerActivity.start(this);
                break;
            case R.id.btn_shortcut:
                ShortcutActivity.start(this);
                break;
            default:
                break;
        }
    }

    private BroadcastReceiver mDynamicReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.d("DynamicReceiver onReceiver, intent = " + intent.toString());
            Toast.makeText(context, "dynamic broadcast receiver", Toast.LENGTH_SHORT).show();
        }
    };

    private void startJobServiceTest() {
        LogUtil.d("startJobServiceTest");
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder = new JobInfo.Builder(1,
                new ComponentName(MainActivity.this, DemoJobService.class));
        builder.setMinimumLatency(TimeUnit.MILLISECONDS.toMillis(5));
        builder.setOverrideDeadline(TimeUnit.MILLISECONDS.toMillis(15));
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setBackoffCriteria(TimeUnit.MINUTES.toMillis(10), JobInfo.BACKOFF_POLICY_LINEAR);
        builder.setRequiresCharging(false);
        jobScheduler.schedule(builder.build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
            }
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS,
                            Manifest.permission.WRITE_CONTACTS},
                    0);
        }
    }
}