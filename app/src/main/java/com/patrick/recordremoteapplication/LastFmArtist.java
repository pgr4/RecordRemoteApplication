package com.patrick.recordremoteapplication;

import android.graphics.Bitmap;

import java.util.Comparator;

/**
 * Created by pat on 12/30/2014.
 */
public class LastFmArtist {
    public Bitmap Bitmap;
    public String Name;

    public LastFmArtist(String name, Bitmap bitmap) {
        Name = name;
        Bitmap = bitmap;
    }

    public static Comparator<LastFmArtist> StuNameComparator = new Comparator<LastFmArtist>() {

        public int compare(LastFmArtist a1, LastFmArtist a2) {
            String a1Name = a1.Name;
            String a2Name = a2.Name;

            //ascending order
            return a1Name.compareTo(a2Name);
        }
    };
}