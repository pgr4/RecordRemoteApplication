package com.patrick.recordremoteapplication;

import android.os.AsyncTask;

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
            //Convert the Stream to a String
            //Create the JSONObject from the string
            JSONObject jso = new JSONObject(LastFmBaseLookup.ConvertStreamToString(response.getEntity().getContent()));
            //Create a JSONArray from the names (This should only get us one object!)
            //Get the "artistmatches" array in our object
            JSONObject artists = LastFmBaseLookup.getFoundResultCount(jso.toJSONArray(jso.names()), "Track");
            JSONArray lst = artists.getJSONArray("track");

            for (int i = 0; i < lst.length(); i++) {
                JSONObject obj = lst.getJSONObject(i);
                songList.add(obj.getString("name"));
            }

            return songList;
        } catch (URISyntaxException | IOException | JSONException e) {
            e.printStackTrace();
        }
        return songList;
    }
}