package com.patrick.recordremoteapplication;

import android.content.ClipData;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by pat on 1/20/2015.
 */
public class JsonArtist implements Parcelable {
    ArrayList<JsonAlbum> Albums;
    String Name;

    JsonArtist() {

    }

    JsonArtist(String name, ArrayList<JsonAlbum> arr) {
        Name = name;
        Albums = arr;
    }

    JsonArtist(Parcel in) {
        Name = in.readString();
        in.readTypedList(Albums,JsonAlbum.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Name);
        dest.writeTypedList(Albums);
    }

    public static final Parcelable.Creator<JsonArtist> CREATOR
            = new Parcelable.Creator<JsonArtist>() {
        public JsonArtist createFromParcel(Parcel in) {
            return new JsonArtist(in);
        }

        public JsonArtist[] newArray(int size) {
            return new JsonArtist[size];
        }
    };

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        Bundle b = new Bundle();
//        b.putString("Name", Name);
//        b.putParcelableArrayList("Albums",Albums);
//        dest.writeBundle(b);
//    }
//
//    public static final Parcelable.Creator<JsonArtist> CREATOR =
//            new Parcelable.Creator<JsonArtist>() {
//                public JsonArtist createFromParcel(Parcel in) {
//                    JsonArtist jsonArtist = new JsonArtist();
//                    Bundle b = in.readBundle(JsonArtist.class.getClassLoader());
//                    jsonArtist.Name = b.getString("Name");
//                    jsonArtist.Albums = b.getParcelableArrayList("Albums");
//                    return jsonArtist;
//                }
//
//                @Override
//                public JsonArtist[] newArray(int size) {
//                    return new JsonArtist[size];
//                }
//            };
}