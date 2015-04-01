package com.patrick.recordremoteapplication;

import android.graphics.Bitmap;

import java.util.Comparator;

/**
 * Created by pat on 12/31/2014.
 */
public class LastFmAlbum {
    public Bitmap Bitmap;
    public String AlbumName;
    public String ArtistName;
    public String ImageUrl;
    public int Order;

    @Override
    public String toString() {
        return AlbumName;
    }


    public LastFmAlbum(String albumName, String artistName, Bitmap bitmap, String imgUrl, int order) {
        AlbumName = albumName;
        ArtistName = artistName;
        Bitmap = bitmap;
        ImageUrl = imgUrl;
        Order = order;
    }

    public static Comparator<LastFmAlbum> StuNameComparator = new Comparator<LastFmAlbum>() {

        public int compare(LastFmAlbum a1, LastFmAlbum a2) {
            String a1Name = a1.AlbumName;
            String a2Name = a2.AlbumName;

            //ascending order
            return a1Name.compareTo(a2Name);

            //descending order
            //return StudentName2.compareTo(StudentName1);
        }
    };
}
