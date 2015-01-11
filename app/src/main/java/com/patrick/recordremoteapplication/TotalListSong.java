package com.patrick.recordremoteapplication;

import android.content.ClipData;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by pat on 1/11/2015.
 */
public class TotalListSong implements Parcelable{
    public ArrayList<String> Songs;
    public String Album;
    public String Artist;

    public TotalListSong(){

    }

    public TotalListSong(ArrayList<String> songs, String album, String artist) {
        Songs = songs;
        Album = album;
        Artist = artist;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //dest.writeString("IDK");
        Bundle b = new Bundle();
        b.putStringArrayList("Songs", Songs);
        b.putString("Artist", Artist);
        b.putString("Album", Album);
        dest.writeBundle(b);
    }

    public static final Parcelable.Creator<TotalListSong> CREATOR =
            new Parcelable.Creator<TotalListSong>() {
                public TotalListSong createFromParcel(Parcel in) {
                    TotalListSong totalListSong = new TotalListSong();
                    //String gasfg = in.readString();
                    Bundle b = in.readBundle(ClipData.Item.class.getClassLoader());
                    totalListSong.Songs = b.getStringArrayList("Songs");
                    totalListSong.Album = b.getString("Album");
                    totalListSong.Artist = b.getString("Artist");
                    return totalListSong;
                }

                @Override
                public TotalListSong[] newArray(int size) {
                    return new TotalListSong[size];
                }
            };
}
