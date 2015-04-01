package com.patrick.recordremoteapplication;

import android.graphics.Bitmap;

/**
 * Created by pat on 4/1/2015.
 */
public class BitmapWithNumber {
    public Bitmap BitmapImage;
    public int Order;

    BitmapWithNumber(Bitmap bitmap, int order) {
        BitmapImage = bitmap;
        Order = order;
    }
}
