package com.patrick.recordremoteapplication;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by pat on 12/31/2014.
 */
public class AlbumListAdapter extends ArrayAdapter<LastFmAlbum> {
    private final Activity context;
    private ArrayList<LastFmAlbum> list;

    public AlbumListAdapter(Activity c, ArrayList<LastFmAlbum> lst) {
        super(c, R.layout.picture_text_list_item, lst);

        list = lst;
        context = c;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.picture_text_list_item, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.textView);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);

        imageView.setImageBitmap(list.get(position).Bitmap);
        txtTitle.setText(list.get(position).AlbumName);
        return rowView;
    }
}
