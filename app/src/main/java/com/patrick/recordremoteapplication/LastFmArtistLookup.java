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
import java.util.ArrayList;
import java.net.URLEncoder;
import java.util.Collections;

/**
 * Created by pat on 12/30/2014.
 */

public class LastFmArtistLookup extends AsyncTask<String, Void, ArrayList<LastFmArtist>>{

    //TODO:CLEAN THIS GARBAGE UP
    protected ArrayList doInBackground(String... strings) {
        //GET START TIME
        Log.d("LastFmLookupTiming",  String.valueOf(System.currentTimeMillis()));
        ArrayList<LastFmArtist> artistList = new ArrayList<LastFmArtist>();

        try {
            //Form the query String
            String query = LastFmBaseLookup.StartQuery + LastFmBaseLookup.artistQuery + URLEncoder.encode(strings[0], "UTF-8") + LastFmBaseLookup.EndQuery;
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
            Log.d("LastFmLookupTiming",  String.valueOf(System.currentTimeMillis()));
            //Create the JSONObject from the string
            JSONObject jso = new JSONObject(res);
            //Get the names from the JSONObject
            JSONArray names = jso.names();
            //Create a JSONArray from the names (This should only get us one object!)
            JSONArray jsonArray = jso.toJSONArray(names);
            //Get the "artistmatches" array in our object
            JSONObject artists = LastFmBaseLookup.getFoundResultCount(jsonArray, "Artist");
            JSONArray lst = artists.getJSONArray("artist");
            //GET START TIME
            Log.d("LastFmLookupTiming",  String.valueOf(System.currentTimeMillis()));
            for (int i = 0; i < lst.length(); i++) {
                JSONObject obj = lst.getJSONObject(i);
                //TODO:USE DEFAULT IMAGE
                String imageUrl = "";
                JSONArray images = obj.getJSONArray("image");
                if (images.length() > 0) {
                    JSONObject imgObj = images.getJSONObject(images.length() - 1);
                    imageUrl = imgObj.getString("#text");
                }

                artistList.add(new LastFmArtist(obj.getString("name"), LastFmBaseLookup.getBitmapFromURL(imageUrl)));
            }
            //GET START TIME
            Log.d("LastFmLookupTiming", String.valueOf(System.currentTimeMillis()));
            Collections.sort(artistList,LastFmArtist.StuNameComparator);
            //GET START TIME
            Log.d("LastFmLookupTiming",  String.valueOf(System.currentTimeMillis()));
            return artistList;
        } catch (URISyntaxException | IOException | JSONException e) {
            e.printStackTrace();
        }
        return artistList;
    }
}
