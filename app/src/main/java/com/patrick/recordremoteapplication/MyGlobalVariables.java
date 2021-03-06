package com.patrick.recordremoteapplication;

import android.app.Application;
import android.graphics.Bitmap;

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
    public BusyStatus Status = BusyStatus.Unknown;
    public Bitmap CurrentBitmap;
    public String CurrentAlbum;
    public String CurrentArtist;
    public ArrayList<String> CurrentSongList;
    public String CurrentSong;
    public int[] CurrentKey;
    public boolean HasAlbum = false;
    public boolean IsPowerOn = false;
    public boolean IsPlaying = false;
    public boolean IsAuto = true ;
}
