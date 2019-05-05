package com.adocker.test.components.notification;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.adocker.test.R;
import com.adocker.test.utils.NotificationUtil;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationTestActivity extends AppCompatActivity implements View.OnClickListener {

    private NotificationManager mNotificationManager;
    private Button mSimple;
    private Button mAction;
    private Button mRemoteInput;
    private Button mBigPictureStyle;
    private Button mBigTextStyle;
    private Button mInboxStyle;
    private Button mMediaStyle;
    private Button mMessagingStyle;
    private Button mProgress;
    private Button mCustomHeadsUp;
    private Button mCustom;
    private Button mClearAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        init();
    }

    private void init() {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannels.createAllNotificationChannels(this);
        }

        mSimple = findViewById(R.id.btn_simple_nf_ch);
        mAction = findViewById(R.id.btn_action_nf_ch);
        mRemoteInput = findViewById(R.id.btn_remote_input_nf_ch);
        mBigPictureStyle = findViewById(R.id.btn_big_picture_style_nf_ch);
        mBigTextStyle = findViewById(R.id.btn_big_text_style_nf_ch);
        mInboxStyle = findViewById(R.id.btn_inbox_style_nf_ch);
        mMediaStyle = findViewById(R.id.btn_media_style_nf_ch);
        mMessagingStyle = findViewById(R.id.btn_messaging_style_nf_ch);
        mProgress = findViewById(R.id.btn_progress_nf_ch);
        mCustomHeadsUp = findViewById(R.id.btn_custom_heads_up_nf_ch);
        mCustom = findViewById(R.id.btn_custom_nf_ch);
        mClearAll = findViewById(R.id.btn_clear_all_nf);

        mSimple.setOnClickListener(this);
        mAction.setOnClickListener(this);
        mRemoteInput.setOnClickListener(this);
        mBigPictureStyle.setOnClickListener(this);
        mBigTextStyle.setOnClickListener(this);
        mInboxStyle.setOnClickListener(this);
        mMediaStyle.setOnClickListener(this);
        mMessagingStyle.setOnClickListener(this);
        mProgress.setOnClickListener(this);
        mCustomHeadsUp.setOnClickListener(this);
        mCustom.setOnClickListener(this);
        mClearAll.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        NotificationUtil notificationUtil = NotificationUtil.getInstance();
        int id = v.getId();
        switch (id) {
            case R.id.btn_simple_nf_ch:
                notificationUtil.sendNotificationChannel(this, "Simple Notification",
                        "Demo for simple notification", true, true,
                        NotificationUtil.NOTIFICATION_SIMPLE, NotificationChannels.LOW, mNotificationManager);
                break;
            case R.id.btn_action_nf_ch:
                notificationUtil.sendNotificationChannel(this, "Action Notification",
                        "Demo for action notification", true, true,
                        NotificationUtil.NOTIFICATION_ACTION, NotificationChannels.DEFAULT, mNotificationManager);
                break;
            case R.id.btn_remote_input_nf_ch:
                notificationUtil.sendNotificationChannel(this, "Remote Notification",
                        "Demo for remote notification", true, true,
                        NotificationUtil.NOTIFICATION_REMOTE_INPUT, NotificationChannels.DEFAULT, mNotificationManager);
                break;
            case R.id.btn_big_picture_style_nf_ch:
                notificationUtil.sendNotificationChannel(this, "Big Picture Notification",
                        "Demo for big picture notification", true, true,
                        NotificationUtil.NOTIFICATION_BIG_PICTURE_STYLE, NotificationChannels.DEFAULT, mNotificationManager);
                break;
            case R.id.btn_big_text_style_nf_ch:
                notificationUtil.sendNotificationChannel(this, "Big Text Notification",
                        "Demo for big text notification", true, true,
                        NotificationUtil.NOTIFICATION_BIG_TEXT_STYLE, NotificationChannels.DEFAULT, mNotificationManager);
                break;
            case R.id.btn_inbox_style_nf_ch:
                notificationUtil.sendNotificationChannel(this, "Inbox Notification",
                        "Demo for inbox notification", true, true,
                        NotificationUtil.NOTIFICATION_INBOX_STYLE, NotificationChannels.DEFAULT, mNotificationManager);
                break;
            case R.id.btn_media_style_nf_ch:
                // NotificationCompat have no media style.
                break;
            case R.id.btn_messaging_style_nf_ch:
                notificationUtil.sendNotificationChannel(this, "Message Notification",
                        "Demo for message notification", true, true,
                        NotificationUtil.NOTIFICATION_MESSAGING_STYLE, NotificationChannels.DEFAULT, mNotificationManager);
                break;
            case R.id.btn_progress_nf_ch:
                notificationUtil.sendNotificationChannel(this, "Progress Notification",
                        "Demo for progress notification", true, true,
                        NotificationUtil.NOTIFICATION_PROGRESS, NotificationChannels.DEFAULT, mNotificationManager);
                break;
            case R.id.btn_custom_heads_up_nf_ch:
                notificationUtil.sendNotificationChannel(this, "Custom Heads Up Notification",
                        "Demo for heads up notification", true, true,
                        NotificationUtil.NOTIFICATION_CUSTOM_HEADS_UP, NotificationChannels.CRITICAL, mNotificationManager);
                break;
            case R.id.btn_custom_nf_ch:
                notificationUtil.sendNotificationChannel(this, "Custom Notification",
                        "Demo for custom notification", true, true,
                        NotificationUtil.NOTIFICATION_CUSTOM, NotificationChannels.MEDIA, mNotificationManager);
                break;
            case R.id.btn_clear_all_nf:
                mNotificationManager.cancelAll();
                break;
            default:
                break;
        }
    }

}
