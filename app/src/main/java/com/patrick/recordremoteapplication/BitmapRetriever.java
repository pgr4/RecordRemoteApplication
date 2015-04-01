package com.patrick.recordremoteapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by pat on 4/1/2015.
 */
public class BitmapRetriever extends AsyncTask<String, Void, BitmapWithNumber> {

    protected BitmapWithNumber doInBackground(String... strings) {
        try {
            URL url = new URL(strings[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

            return new BitmapWithNumber(myBitmap, Integer.parseInt(strings[1]));
        } catch (IOException e) {
            return null;
        }
    }
}