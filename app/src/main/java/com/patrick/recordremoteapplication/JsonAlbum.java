package com.patrick.recordremoteapplication;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by pat on 1/20/2015.
 */
public class JsonAlbum {
    String Name;
    String Image;
    String Key;

    JsonAlbum(String name, String image,String key) {
        Name = name;
        Image = image;
        Key = key;
    }
}
