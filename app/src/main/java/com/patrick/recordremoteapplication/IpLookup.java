package com.patrick.recordremoteapplication;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.InetAddress;

/**
 * Created by pat on 2/1/2015.
 */
public class IpLookup extends AsyncTask<String, Void, InetAddress> {

    @Override
    protected InetAddress doInBackground(String... params) {
        try {
            return InetAddress.getByName((params[0]).replace("/", ""));
        } catch (Exception e) {
            return null;
        }
    }
}
