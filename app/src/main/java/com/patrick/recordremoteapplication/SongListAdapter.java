package com.patrick.recordremoteapplication;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by pat on 12/31/2014.
 */
public class SongListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private ArrayList<String> list;

    public SongListAdapter(Activity c, ArrayList<String> lst) {
        super(c, R.layout.editable_number_song_list_item, lst);
        list = lst;
        context = c;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.editable_number_song_list_item, null, true);

        TextView txtNumber = (TextView) rowView.findViewById(R.id.textViewNumber);
        EditText txtSong = (EditText) rowView.findViewById(R.id.editTextSong);


        txtSong.setText(list.get(position));
        txtNumber.setText(String.valueOf(position + 1));

        return rowView;
    }
}