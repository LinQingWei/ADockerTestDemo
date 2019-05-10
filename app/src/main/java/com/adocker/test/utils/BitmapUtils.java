package com.adocker.test.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public final class BitmapUtils {

    private BitmapUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static Bitmap wrapperIcon(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width < newWidth || height < newHeight) {
            return bitmap;
        }
        float scaleWidth = ((float) newWidth) / ((float) width);
        float scaleHeight = ((float) newHeight) / ((float) height);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }
}
