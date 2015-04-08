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
    public String csSongs;

    public SongListAdapter(Activity c, ArrayList<String> lst) {
        super(c, R.layout.current_list_item, lst);
        list = lst;
        context = c;
        csSongs = listToString(lst);
    }

    @Override
    public void remove(String x){
        for(int i = 0;i<list.size();i++){
            if(x.equals(list.get(i))){
                list.remove(i);
                break;
            }
        }
        csSongs = listToString(list);
    }

    private String listToString(ArrayList<String> arr) {
        String ret = "";
        for (int i = 0; i < arr.size(); i++) {
            if (i != 0) {
                ret += ",";
            }
            ret += arr.get(i);
        }
        return ret;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.current_list_item, null, true);

        TextView txtNumber = (TextView) rowView.findViewById(R.id.textViewNumber);
        TextView txtSong = (TextView) rowView.findViewById(R.id.editTextSong);


        txtSong.setText(list.get(position));
        txtNumber.setText(String.valueOf(position + 1));

        return rowView;
    }
}