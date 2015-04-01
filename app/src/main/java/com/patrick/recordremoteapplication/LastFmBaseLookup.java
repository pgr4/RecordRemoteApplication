package com.patrick.recordremoteapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by pat on 12/31/2014.
 */
public class LastFmBaseLookup {
    public static String StartQuery = "http://ws.audioscrobbler.com/2.0/?";
    public static String EndQuery = "&api_key=557e6ea3fad888bd915f713613941051&format=json";
    public static String artistQuery = "method=artist.search&artist=";
    public static String albumQuery = "method=artist.gettopalbums&artist=";
    public static String albumInfoQuery = "method=album.getinfo&";
    public static String albumInfoArtistSubQuery = "artist=";
    public static String albumInfoAlbumSubQuery = "&album=";

    public static JSONObject getFoundResultCount(JSONArray jsa, String type) throws JSONException {
        switch (type) {
            case "Artist":
                if (jsa.length() == 1) {
                    JSONObject value = jsa.getJSONObject(0);
                    return value.getJSONObject("artistmatches");
                }
                break;
            case "Track":
                if (jsa.length() == 1) {
                    JSONObject value = jsa.getJSONObject(0);
                    return value.getJSONObject("tracks");
                }
                break;
            default:
                break;
        }
        return null;
    }

    public static String ConvertStreamToString(InputStream inputStream) throws IOException {

        if (inputStream != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 1024);
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                inputStream.close();
            }

            return writer.toString();
        } else {
            return "";
        }
    }

}
