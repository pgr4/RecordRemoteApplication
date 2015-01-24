package com.patrick.recordremoteapplication;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by pat on 1/20/2015.
 */
public class JsonAlbum implements Parcelable {
    String Name;
    String Image;
    ArrayList<String> Songs;

    JsonAlbum(String name, String image, ArrayList<String> arr) {
        Name = name;
        Image = image;
        Songs = arr;
    }

    JsonAlbum(Parcel in) {
        readFromParcel(in);
    }

    private void readFromParcel(Parcel in) {
        Name = in.readString();
        Image = in.readString();
        if(Songs == null){
            Songs = new ArrayList<>();
        }
        in.readStringList(Songs);
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Name);
        dest.writeString(Image);
        dest.writeStringList(Songs);
    }

    public static final Parcelable.Creator<JsonAlbum> CREATOR = new Parcelable.Creator<JsonAlbum>() {

        @Override
        public JsonAlbum createFromParcel(Parcel in) {
            return new JsonAlbum(in);
        }

        @Override
        public JsonAlbum[] newArray(int size) {
            return new JsonAlbum[size];
        }
    };
}
