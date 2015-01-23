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
public class TotalListAdapter extends ArrayAdapter<JsonArtist> {
    private final Activity context;
    private ArrayList<JsonArtist> list;

    public TotalListAdapter(Activity c, ArrayList<JsonArtist> lst) {
        super(c, R.layout.total_list_item, lst);
        list = lst;
        context = c;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.total_list_item, null, true);

        TextView txtArtist = (TextView) rowView.findViewById(R.id.txtArtist);

        txtArtist.setText(list.get(position).Name);

        LinearLayout linearLayout = (LinearLayout) rowView.findViewById(R.id.linearLayout);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        for(int i = 0; i< list.get(position).Albums.size();i++){
            LinearLayout ll = new LinearLayout(getContext());
            TextView albumText = new TextView(getContext());
            albumText.setText(list.get(position).Albums.get(i).Name);
            albumText.setLayoutParams(lp);
            ll.addView(albumText);
            for(int j=0;j<list.get(position).Albums.get(i).Songs.size();j++){
                TextView songText = new TextView(getContext());
                songText.setText(list.get(position).Albums.get(i).Songs.get(j).toString());
                songText.setLayoutParams(lp);
                ll.addView(songText);
            }
            linearLayout.addView(ll);
        }

        return rowView;
    }
}
