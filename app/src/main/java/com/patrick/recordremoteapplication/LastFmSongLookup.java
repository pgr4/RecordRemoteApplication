package com.patrick.recordremoteapplication;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by pat on 12/31/2014.
 */
public class LastFmSongLookup extends AsyncTask<String, Void, ArrayList<String>> {
    protected ArrayList doInBackground(String... strings) {
        //GET START TIME
        Log.d("LastFmLookupTiming", String.valueOf(System.currentTimeMillis()));
        ArrayList<String> songList = new ArrayList<>();

        try {
            //Form the query String
            String query = LastFmBaseLookup.StartQuery + LastFmBaseLookup.albumInfoQuery +
                    LastFmBaseLookup.albumInfoArtistSubQuery + URLEncoder.encode(strings[0], "UTF-8") +
                    LastFmBaseLookup.albumInfoAlbumSubQuery + URLEncoder.encode(strings[1], "UTF-8") +
                    LastFmBaseLookup.EndQuery;

            HttpResponse response = null;
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            //Set URI
            request.setURI(new URI(query));
            //Execute the request
            response = client.execute(request);
            //Get the InputStream from the response
            InputStream s = response.getEntity().getContent();
            //Convert the Stream to a String
            String res = LastFmBaseLookup.ConvertStreamToString(s);
            //GET START TIME
            Log.d("LastFmLookupTiming", String.valueOf(System.currentTimeMillis()));
            //Create the JSONObject from the string
            JSONObject jso = new JSONObject(res);
            //Get the names from the JSONObject
            JSONArray names = jso.names();
            //Create a JSONArray from the names (This should only get us one object!)
            JSONArray jsonArray = jso.toJSONArray(names);
            //Get the "artistmatches" array in our object
            JSONObject artists = LastFmBaseLookup.getFoundResultCount(jsonArray, "Track");
            JSONArray lst = artists.getJSONArray("track");
            //GET START TIME
            Log.d("LastFmLookupTiming", String.valueOf(System.currentTimeMillis()));
            for (int i = 0; i < lst.length(); i++) {
                JSONObject obj = lst.getJSONObject(i);
                songList.add(obj.getString("name"));
            }
            //GET START TIME
            Log.d("LastFmLookupTiming", String.valueOf(System.currentTimeMillis()));
            return songList;
        } catch (URISyntaxException | IOException | JSONException e) {
            e.printStackTrace();
        }
        return songList;
    }
}