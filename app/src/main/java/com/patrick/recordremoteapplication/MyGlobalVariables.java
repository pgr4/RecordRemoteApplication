package com.patrick.recordremoteapplication;

import android.app.Application;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by pat on 1/24/2015.
 */
public class MyGlobalVariables extends Application {

    public InetAddress DatabaseIp;
    public ArrayList<String> Artists;
    public ArrayList<JsonAlbum> Albums;
    public String TotalAlbumImage;
    public InetAddress MyIp;
    public StatusType Status;
    public String StatusExtra;
}
