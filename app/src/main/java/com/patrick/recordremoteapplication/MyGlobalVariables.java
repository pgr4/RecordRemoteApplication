package com.patrick.recordremoteapplication;

import android.app.Application;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by pat on 1/24/2015.
 */
public class MyGlobalVariables extends Application {

    public InetAddress MyIp;
    public ArrayList<JsonArtist> Artists;
}
