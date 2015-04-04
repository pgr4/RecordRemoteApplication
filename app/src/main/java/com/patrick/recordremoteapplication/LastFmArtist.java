package com.patrick.recordremoteapplication;

import android.graphics.Bitmap;

import java.util.Comparator;

/**
 * Created by pat on 12/30/2014.
 */
public class LastFmArtist {
    public Bitmap Bitmap;
    public String Name;
    public int Order;
    public int PostOrder;

    public LastFmArtist(String name, Bitmap bitmap, int order) {
        Name = name;
        Bitmap = bitmap;
        Order = order;
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
