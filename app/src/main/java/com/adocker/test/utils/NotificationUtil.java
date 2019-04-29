package com.adocker.test.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.widget.RemoteViews;

import com.adocker.test.R;
import com.adocker.test.components.notification.LaunchActivity;
import com.adocker.test.components.notification.NotificationContentWrapper;

import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

public class NotificationUtil {

    public final static int NOTIFICATION_SIMPLE = 1;
    public final static int NOTIFICATION_ACTION = 2;
    public final static int NOTIFICATION_REMOTE_INPUT = 3;
    public final static int NOTIFICATION_BIG_PICTURE_STYLE = 4;
    public final static int NOTIFICATION_BIG_TEXT_STYLE = 5;
    public final static int NOTIFICATION_INBOX_STYLE = 6;
    public final static int NOTIFICATION_MEDIA_STYLE = 7;
    public final static int NOTIFICATION_MESSAGING_STYLE = 8;
    public final static int NOTIFICATION_PROGRESS = 9;
    public final static int NOTIFICATION_CUSTOM_HEADS_UP = 10;
    public final static int NOTIFICATION_CUSTOM = 11;

    public final static String REMOTE_INPUT_RESULT_KEY = "remote_input_content";

    private static NotificationUtil mNotificationUtil;

    public static NotificationUtil getInstance() {
        if (mNotificationUtil == null) {
            mNotificationUtil = new NotificationUtil();
        }
        return mNotificationUtil;
    }

    public void sendNotification(Context context, String title, String content, boolean autoCancel,
                                 boolean showWhen, int id, NotificationManager nm) {
        sendNotificationChannel(context, title, content, autoCancel, showWhen, id, null, nm);
    }

    public void sendNotificationChannel(Context context, String title, String content, boolean autoCancel,
                                        boolean showWhen, int id, String channelId, NotificationManager nm) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_notification)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(autoCancel)
                .setShowWhen(showWhen);
        switch (id) {
            case NOTIFICATION_SIMPLE:
                builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_notifiation_big));
                break;
            case NOTIFICATION_ACTION:
                NotificationCompat.Action aciton = new NotificationCompat.Action.Builder(
                        R.mipmap.ic_yes, "YES",
                        PendingIntent.getService(context, 0, new Intent(), 0))
                        .build();
                builder.addAction(aciton);
                break;
            case NOTIFICATION_REMOTE_INPUT:
                RemoteInput remoteInput = new RemoteInput.Builder(REMOTE_INPUT_RESULT_KEY)
                        .setLabel("Reply").build();
                PendingIntent replyPendingIntent = PendingIntent.getService(context, 2, new Intent(), 0);
                NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                        R.mipmap.ic_reply,
                        "Reply",
                        replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();
                builder.addAction(replyAction);
                break;
            case NOTIFICATION_BIG_PICTURE_STYLE:
                NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle()
                        .setBigContentTitle("Big picture style notification ~ Expand title")
                        .setSummaryText("Demo for big picture style notification ! ~ Expand summery")
                        .bigPicture(BitmapFactory.decodeResource(context.getResources(), R.mipmap.big_style_picture));
                builder.setStyle(bigPictureStyle);
                break;
            case NOTIFICATION_BIG_TEXT_STYLE:
                NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                        .setBigContentTitle("Big text style notification ~ Expand title")
                        .setSummaryText("Demo for big text style notification ! ~ Expand summery")
                        .bigText("We are the champions   \n" +
                                "We are the champions   \n" +
                                "No time for losers   \n" +
                                "Cause we are the champions of the World");
                builder.setStyle(bigTextStyle);
                break;
            case NOTIFICATION_INBOX_STYLE:
                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle()
                        .setBigContentTitle("Inbox style notification ~ Expand title")
                        .setSummaryText("Demo for inbox style notification ! ~ Expand summery")
                        .addLine("1. I am email content.")
                        .addLine("2. I am email content.")
                        .addLine("3. I am email content.")
                        .addLine("4. I am email content.")
                        .addLine("5. I am email content.")
                        .addLine("6. I am email content.");
                builder.setStyle(inboxStyle);
                break;
            case NOTIFICATION_MEDIA_STYLE:
                // NotificationCompat have no media style.
                break;
            case NOTIFICATION_MESSAGING_STYLE:
                NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle("peter")
                        .setConversationTitle("Messaging style notification")
                        .addMessage("This is a message for you", System.currentTimeMillis(), "peter");
                builder.setStyle(messagingStyle);
                break;
            case NOTIFICATION_PROGRESS:
                builder.setContentText("10" + "%");
                builder.setProgress(100, 10, false);
                break;
            case NOTIFICATION_CUSTOM_HEADS_UP:
                Intent intent = new Intent(context, LaunchActivity.class);
                PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
                PendingIntent answerPendingIntent = PendingIntent.getService(context, 0,
                        new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
                PendingIntent rejectPendingIntent = PendingIntent.getService(context, 1,
                        new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
                RemoteViews headsUpView = new RemoteViews(context.getPackageName(), R.layout.custom_heads_up_layout);
                headsUpView.setOnClickPendingIntent(R.id.iv_answer, answerPendingIntent);
                headsUpView.setOnClickPendingIntent(R.id.iv_reject, rejectPendingIntent);
                builder.setContentIntent(pi)
                        .setFullScreenIntent(pi, true)
                        .setCustomHeadsUpContentView(headsUpView);
                break;
            case NOTIFICATION_CUSTOM:
                NotificationContentWrapper nw = new NotificationContentWrapper(
                        BitmapFactory.decodeResource(context.getResources(), R.mipmap.custom_view_picture_current),
                        "xxxxxxxxxx", "xxx - xxxxx");
                RemoteViews customView = new RemoteViews(context.getPackageName(), R.layout.custom_view_layout);
                customView.setImageViewBitmap(R.id.iv_content, nw.bitmap);
                customView.setTextViewText(R.id.tv_title, nw.title);
                customView.setTextViewText(R.id.tv_summery, nw.summery);
                customView.setImageViewBitmap(R.id.iv_play_or_pause, BitmapFactory.decodeResource(context.getResources(),
                        true ? R.mipmap.ic_pause : R.mipmap.ic_play));
                RemoteViews customBigView = new RemoteViews(context.getPackageName(), R.layout.custom_big_view_layout);
                customBigView.setImageViewBitmap(R.id.iv_content_big, nw.bitmap);
                customBigView.setTextViewText(R.id.tv_title_big, nw.title);
                customBigView.setTextViewText(R.id.tv_summery_big, nw.summery);
                customBigView.setImageViewBitmap(R.id.iv_love_big, BitmapFactory.decodeResource(context.getResources(),
                        false ? R.mipmap.ic_loved : R.mipmap.ic_love));
                customBigView.setImageViewBitmap(R.id.iv_play_or_pause_big, BitmapFactory.decodeResource(context.getResources(),
                        true ? R.mipmap.ic_pause : R.mipmap.ic_play));
                builder.setCustomContentView(customView)
                        .setCustomBigContentView(customBigView);
                break;
            default:
                break;
        }

        nm.notify(id, builder.build());
    }
}
