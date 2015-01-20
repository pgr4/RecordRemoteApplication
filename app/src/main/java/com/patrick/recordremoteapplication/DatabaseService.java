package com.patrick.recordremoteapplication;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by pat on 1/5/2015.
 */
public class DatabaseService extends IntentService {
    public DatabaseService() {
        super("DatabaseService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            try {
                Bundle b = intent.getExtras();

                switch (b.getString("type")) {
                    case "getAllSongs":
                        getAllSongs();
                        break;
                    case "getAlbum":
                        getAlbum(b.getByteArray("key"));
                        break;
                    case "getAlbumSongs":
                        getAlbumSongs(b.getByteArray("key"));
                        break;
                    case "addAlbumData":
                        addAlbumData(b.getByteArray("key"),
                                b.getString("songs"),
                                b.getString("artist"),
                                b.getString("album"),
                                b.getString("image"));

                        break;
                    default:
                        throw new Exception("missed");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //Create a HTTP GET Request to get all Songs
    private void getAllSongs() throws IOException, JSONException {
        //Form the query String
        String query = "http://192.168.1.247/api/Song";
        HttpResponse response;
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(query);
        //Set URI
        //request.setURI(new URI(query));
        //Execute the request
        response = client.execute(request);
        //Get the InputStream from the response
        InputStream s = response.getEntity().getContent();
        //Convert the Stream to a String
        String res = LastFmBaseLookup.ConvertStreamToString(s);
        goToTotalSongListScreen(StringToSongArr(res));
    }

    //Create a HTTP GET Request to get an album
    private void getAlbum(byte[] by) throws IOException {
        byte[] bytes = {(byte) 0x54, (byte) 0x54};
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        //Form the query String
        String query = "http://192.168.1.247/api/album?s=" + sb;
        HttpResponse response = null;
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(query);
        //Set URI
        //request.setURI(new URI(query));
        //Execute the request
        response = client.execute(request);
        //Get the InputStream from the response
        InputStream s = response.getEntity().getContent();
        //Convert the Stream to a String
        String res = LastFmBaseLookup.ConvertStreamToString(s);
    }

    //Create a HTTP GET Request to get all Songs associated with the album
    private void getAlbumSongs(byte[] by) throws IOException {
        byte[] bytes = {(byte) 0x54, (byte) 0x54};
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        //Form the query String
        String query = "http://192.168.1.247/api/Song?s=" + sb;
        HttpResponse response = null;
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(query);
        //Set URI
        //request.setURI(new URI(query));
        //Execute the request
        response = client.execute(request);
        //Get the InputStream from the response
        InputStream s = response.getEntity().getContent();
        //Convert the Stream to a String
        String res = LastFmBaseLookup.ConvertStreamToString(s);
    }

    //Create a HTTP POST Request with album and song data from a new album
    private void addAlbumData(byte[] key, String songs, String artist, String album, String image) throws IOException, URISyntaxException, JSONException {
        Bitmap bitmap = LastFmBaseLookup.getBitmapFromURL(image);
        StringBuilder sb = new StringBuilder();
        for (byte b : key) {
            sb.append(String.format("%02X", b));
        }
        //Form the query String
        String query = "http://192.168.1.247/api/Song";
        HttpResponse response = null;
        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(query);

        JSONObject Song = new JSONObject();
        Song.put("Key", sb);
        Song.put("Songs", songs);
        Song.put("Artist", artist);
        Song.put("Album", album);
        Song.put("Image", bitmap);

        StringEntity entity = new StringEntity(Song.toString(), HTTP.UTF_8);
        entity.setContentType("application/json");

        request.setEntity(entity);

        //Execute the request
        response = client.execute(request);
        //Get the InputStream from the response
        InputStream s = response.getEntity().getContent();
        //Convert the Stream to a String
        String res = LastFmBaseLookup.ConvertStreamToString(s);

        //Bring up the CurrentListScreen
        goToCurrentSongListScreen(songs, artist, album, bitmap);
    }

    private void goToCurrentSongListScreen(String songs, String artist, String album, Bitmap bitmap) {
        Intent intent = new Intent(this, CurrentListScreen.class);
        //intent.putExtra("newAlbumKey", key);
        intent.putExtra("image", bitmap);
        intent.putExtra("songs", songs);
        intent.putExtra("album", album);
        intent.putExtra("artist", artist);
        //This is necessary
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void goToTotalSongListScreen(ArrayList<JsonArtist> list) {
        Intent intent = new Intent(this, TotalListScreen.class);
        intent.putParcelableArrayListExtra("list", list);
        //This is necessary
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private ArrayList<JsonArtist> StringToSongArr(String s) throws JSONException {
        ArrayList<JsonArtist> ret = new ArrayList<>();

        JSONArray jsonArray = new JSONArray(s);
        String artistName;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonArtist = jsonArray.getJSONObject(i);
            JSONArray jsonAlbums = jsonArtist.getJSONArray("Albums");
            artistName = jsonArtist.getString("Name");

            String albumName;
            String image;
            ArrayList<JsonAlbum> albumArr = new ArrayList<>();
            for (int j = 0; j < jsonAlbums.length(); j++) {
                ArrayList<String> songArr = new ArrayList<>();
                JSONObject jsonAlbum = jsonAlbums.getJSONObject(j);
                albumName = jsonAlbum.getString("Name");
                image = jsonAlbum.getString("Image");
                JSONArray jsonSong = jsonAlbum.getJSONArray("Songs");
                for (int w = 0; w < jsonSong.length(); w++) {
                    songArr.add(jsonSong.get(w).toString());
                }

                albumArr.add(new JsonAlbum(albumName,image,songArr));
            }
            ret.add(new JsonArtist(artistName,albumArr));
        }
        return ret;
    }
}
