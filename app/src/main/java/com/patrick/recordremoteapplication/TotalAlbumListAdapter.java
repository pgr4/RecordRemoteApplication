package com.patrick.recordremoteapplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by pat on 1/25/2015.
 */
public class TotalAlbumListAdapter extends ArrayAdapter<JsonAlbum> {
    private final Activity context;
    private ArrayList<JsonAlbum> list;

    public TotalAlbumListAdapter(Activity c, ArrayList<JsonAlbum> lst) {
        super(c, R.layout.picture_text_list_item, lst);

        list = lst;
        context = c;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.picture_text_list_item, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.textView);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);

        byte[] decodedString = Base64.decode(list.get(position).Image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        imageView.setImageBitmap(decodedByte);
        txtTitle.setText(list.get(position).Name);
        return rowView;
    }
}
