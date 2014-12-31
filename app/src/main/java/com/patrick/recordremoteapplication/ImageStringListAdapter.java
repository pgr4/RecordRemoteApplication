package com.patrick.recordremoteapplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by pat on 12/30/2014.
 */
public class ImageStringListAdapter extends ArrayAdapter<String>{
    private final Activity context;
    private final String[] itemName;
    private final Bitmap[] imgId;

    public ImageStringListAdapter(Activity c, String[] itemname, Bitmap[] imgid) {
        super(c, R.layout.artist_item, itemname);

        context = c;
        itemName = itemname;
        imgId = imgid;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.artist_item, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.textView);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);

        imageView.setImageBitmap(imgId[position]);
        txtTitle.setText(itemName[position]);
        return rowView;
    }

}
