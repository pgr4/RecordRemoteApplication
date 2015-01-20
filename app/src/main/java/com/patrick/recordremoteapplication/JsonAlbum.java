package com.patrick.recordremoteapplication;

import android.content.ClipData;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by pat on 1/20/2015.
 */
public class JsonAlbum implements Parcelable {
    String Name;
    String Image;
    ArrayList Songs;

    JsonAlbum() {

    }

    JsonAlbum(String name, String image, ArrayList<String> arr) {
        Name = name;
        Image = image;
        Songs = arr;
    }

    JsonAlbum(Parcel in) {
        Name = in.readString();
        Image = in.readString();
        Songs = in.readArrayList(String.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Name);
        dest.writeString(Image);
        dest.writeList(Songs);
    }

    public static final Parcelable.Creator<JsonAlbum> CREATOR
            = new Parcelable.Creator<JsonAlbum>() {
        public JsonAlbum createFromParcel(Parcel in) {
            return new JsonAlbum(in);
        }

        public JsonAlbum[] newArray(int size) {
            return new JsonAlbum[size];
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
//        b.putString("Image", Image);
//        b.putStringArrayList("Songs", Songs);
//        dest.writeBundle(b);
//    }
//
//    public static final Parcelable.Creator<JsonAlbum> CREATOR =
//            new Parcelable.Creator<JsonAlbum>() {
//                public JsonAlbum createFromParcel(Parcel in) {
//                    JsonAlbum jsonAlbum = new JsonAlbum();
//                    Bundle b = in.readBundle(JsonAlbum.class.getClassLoader());
//                    jsonAlbum.Name = b.getString("Name");
//                    jsonAlbum.Image = b.getString("Image");
//                    jsonAlbum.Songs = b.getStringArrayList("Songs");
//                    return jsonAlbum;
//                }
//
//                @Override
//                public JsonAlbum[] newArray(int size) {
//                    return new JsonAlbum[size];
//                }
//            };
}
