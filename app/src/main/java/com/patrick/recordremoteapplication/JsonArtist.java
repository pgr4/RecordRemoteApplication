package com.patrick.recordremoteapplication;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by pat on 1/20/2015.
 */
public class JsonArtist implements Parcelable {
    ArrayList<JsonAlbum> Albums;
    String Name;

    JsonArtist(String name, ArrayList<JsonAlbum> arr) {
        Name = name;
        Albums = arr;
    }

    JsonArtist(Parcel in) {
        readFromParcel(in);
    }

    private void readFromParcel(Parcel in) {
        Name = in.readString();
        if (Albums == null){
            Albums = new ArrayList<>();
        }
       in.readTypedList(Albums, JsonAlbum.CREATOR);
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Name);
        dest.writeTypedList(Albums);
    }

    public static final Parcelable.Creator<JsonArtist> CREATOR = new Parcelable.Creator<JsonArtist>() {

        @Override
        public JsonArtist createFromParcel(Parcel in) {
            return new JsonArtist(in);
        }

        @Override
        public JsonArtist[] newArray(int size) {
            return new JsonArtist[size];
        }
    };
}