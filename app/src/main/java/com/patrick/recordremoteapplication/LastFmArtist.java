package com.patrick.recordremoteapplication;

import android.graphics.Bitmap;

/**
 * Created by pat on 12/30/2014.
 */
public class LastFmArtist {
    public Bitmap Bitmap;
    public String Name;

    @Override
    public String toString(){
        return Name;
    }


    public LastFmArtist(String name, Bitmap bitmap) {
        Name = name;
        Bitmap = bitmap;
    }
}
