package com.patrick.recordremoteapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.net.URLEncoder;

/**
 * Created by pat on 12/30/2014.
 */
public class LastFmLookup extends AsyncTask<String, Void, ArrayList<LastFmArtist>> {

    private static String StartQuery = "http://ws.audioscrobbler.com/2.0/?";
    private static String EndQuery = "&api_key=557e6ea3fad888bd915f713613941051&format=json";
    private static String artistQuery = "method=artist.search&artist=";
    private static String albumQuery = "method=album.search&album=";
    private static String albumInfoQuery = "method=album.getinfo&";///needs more

    //TODO:CLEAN THIS GARBAGE UP
    protected ArrayList<LastFmArtist> doInBackground(String... strings) {
        ArrayList<LastFmArtist> artistList = new ArrayList<LastFmArtist>();
        try {
            //Form the query String
            String query = StartQuery + artistQuery + URLEncoder.encode(strings[1], "UTF-8") + EndQuery;
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
            String res = ConvertStreamToString(s);
            //Create the JSONObject from the string
            JSONObject jso = new JSONObject(res);
            //Get the names from the JSONObject
            JSONArray names = jso.names();
            //Create a JSONArray from the names (This should only get us one object!)
            JSONArray jsonArray = jso.toJSONArray(names);
            //Get the "artistmatches" array in our object
            JSONObject artists = getFoundResultCount(jsonArray, "Artist");
            JSONArray lst = artists.getJSONArray("artist");
            for (int i = 0; i < lst.length(); i++) {
                JSONObject obj = lst.getJSONObject(i);
                //TODO:USE DEFAULT IMAGE
                String imageUrl = "";
                JSONArray images = obj.getJSONArray("image");
                if (images.length() > 0) {
                    JSONObject imgObj = images.getJSONObject(images.length() - 1);
                    imageUrl = imgObj.getString("#text");
                }
                artistList.add(new LastFmArtist(obj.getString("name"), getBitmapFromURL(imageUrl)));
            }
            return artistList;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return artistList;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            //TODO:LOG EXCEPTION
            return null;
        }
    }

    //TODO:PUT GARBAGE HERE
    private static JSONObject getFoundResultCount(JSONArray jsa, String type) throws JSONException {
        switch (type) {
            case "Artist":
                if (jsa.length() == 1) {
                    JSONObject value = jsa.getJSONObject(0);
                    return value.getJSONObject("artistmatches");
                }
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
