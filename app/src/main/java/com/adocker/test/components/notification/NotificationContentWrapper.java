package com.adocker.test.components.notification;

import android.graphics.Bitmap;

public class NotificationContentWrapper {
    public Bitmap bitmap;
    public String title;
    public String summery;

    public NotificationContentWrapper(Bitmap bitmap, String title, String summery) {
        this.bitmap = bitmap;
        this.title = title;
        this.summery = summery;
    }
}