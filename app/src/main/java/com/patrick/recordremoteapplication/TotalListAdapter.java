package com.patrick.recordremoteapplication;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by pat on 1/11/2015.
 */
public class TotalListAdapter extends ArrayAdapter<TotalListSong> {
    private final Activity context;
    private ArrayList<TotalListSong> list;

    public TotalListAdapter(Activity c, ArrayList<TotalListSong> lst) {
        super(c, R.layout.total_list_item, lst);
        list = lst;
        context = c;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.total_list_item, null, true);

        TextView txtArtist = (TextView) rowView.findViewById(R.id.txtArtist);
        TextView txtAlbum = (TextView) rowView.findViewById(R.id.txtAlbum);

        txtArtist.setText(list.get(position).Artist);
        txtAlbum.setText(list.get(position).Album);

        LinearLayout linearLayout = (LinearLayout) rowView.findViewById(R.id.linearLayout);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        for(int i = 0; i< list.get(position).Songs.size();i++){
            TextView t = new TextView(getContext());
            t.setText(list.get(position).Songs.get(i));
            t.setLayoutParams(lp);
            linearLayout.addView(t);
        }

        return rowView;
    }
}
