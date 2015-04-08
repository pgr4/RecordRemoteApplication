package com.patrick.recordremoteapplication;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

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
                    case "getAllArtists":
                        getTotalArtists();
                        break;
                    case "getAlbums":
                        getTotalAlbums(b.getString("artist"));
                        break;
                    case "getSongs":
                        getTotalSongs(b.getString("key"));
                        break;
                    case "syncAlbum":
                        syncAlbum(b.getByteArray("key"));
                        break;
                    case "getAlbumSongs":
                        getAlbumSongs(b.getByteArray("key"));
                        break;
                    case "isAlbumNew":
                        findAlbum(b.getByteArray("key"), b.getInt("breaks"));
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

    //Create an HTTP Get Request to get all Artists
    private void getTotalArtists() throws IOException, JSONException {
        //Form the query String
        String ip = ((MyGlobalVariables) getApplication()).DatabaseIp.toString();
        String query = "http:/" + ip + "/api/Artist";
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
        JSONArray jsonArray = new JSONArray(res);
        ArrayList<String> artists = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            artists.add(jsonArray.get(i).toString());
        }
        ((MyGlobalVariables) this.getApplication()).Artists = artists;
        goToTotalArtistScreen();
    }

    //Create an HTTP Get Request to get albums associated with the Artist
    private void getTotalAlbums(String artist) throws IOException, JSONException {
        //Form the query String
        String ip = ((MyGlobalVariables) getApplication()).DatabaseIp.toString();
        String query = "http:/" + ip + "/api/Album?artist=" + URLEncoder.encode(artist, "UTF-8");
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
        JSONArray jsonArray = new JSONArray(res);
        ArrayList<JsonAlbum> albums = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsAlbum = jsonArray.getJSONObject(i);
            albums.add(new JsonAlbum(jsAlbum.getString("Name"), jsAlbum.getString("Image"), jsAlbum.getString("Key")));
        }

        ((MyGlobalVariables) this.getApplication()).Albums = albums;

        goToTotalAlbumScreen();
    }

    //Create an HTTP Get Request to get albums associated with the Artist
    private void getTotalSongs(String key) throws IOException, JSONException {
        //Form the query String
        String ip = ((MyGlobalVariables) getApplication()).DatabaseIp.toString();
        String query = "http:/" + ip + "/api/Song?key=" + URLEncoder.encode(key, "UTF-8");
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

        ArrayList<String> songs = new ArrayList<>();

        JSONObject js = new JSONObject(res);

        JSONArray jsSongs = js.getJSONArray("Songs");
        for (int i = 0; i < jsSongs.length(); i++) {
            songs.add((String) jsSongs.get(i));
        }

        JSONObject jsAlbum = js.getJSONObject("Album");
        ((MyGlobalVariables) this.getApplication()).TotalAlbumImage = jsAlbum.getString("Image");

        goToTotalSongScreen(jsAlbum.getString("Artist"), jsAlbum.getString("Album"), songs);
    }

    //Create an HTTP GET Request to get an album
    private void syncAlbum(byte[] bytes) throws IOException, JSONException {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        //Form the query String
        String ip = ((MyGlobalVariables) getApplication()).DatabaseIp.toString();
        String query = "http:/" + ip + "/api/album?s=" + sb;
        HttpResponse response = null;
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(query);
        //Execute the request
        response = client.execute(request);
        //Get the InputStream from the response
        //Convert the Stream to a String
        String res = LastFmBaseLookup.ConvertStreamToString(response.getEntity().getContent());

        JSONObject jsAlbum = new JSONObject(res);

        String songs = "";

        JSONArray jsSongs = jsAlbum.getJSONArray("Songs");
        for (int i = 0; i < jsSongs.length(); i++) {
            songs += (String) jsSongs.get(i) + ",";
        }

        //TODO: THIS IS BAD PRACTICE YOU SHOULD BE ASHAMED!
        songs += "REMOVETHIS";

        songs = songs.replace(",REMOVETHIS", "");

        byte[] decodedString = Base64.decode(jsAlbum.getString("Image"), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        ((MyGlobalVariables) getApplication()).CurrentBitmap = decodedByte;

        goToCurrentSongListScreen(songs, jsAlbum.getString("Artist"),jsAlbum.getString("Name"), bytes);
    }

    //Create an HTTP GET Request to get an album
    //Used after getting a newAlbum message to check if there is a match in the database
    private void findAlbum(byte[] bytes, int breaks) throws IOException, JSONException {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        //Form the query String
        String ip = ((MyGlobalVariables) getApplication()).DatabaseIp.toString();
        String query = "http:/" + ip + "/api/album?s=" + sb;
        HttpResponse response = null;
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(query);
        //Execute the request
        response = client.execute(request);
        //Get the InputStream from the response
        //Convert the Stream to a String
        String res = LastFmBaseLookup.ConvertStreamToString(response.getEntity().getContent());
        if (res.equals("null")) {
            Intent intent = new Intent(this, ArtistAssociationScreen.class);
            intent.putExtra("newAlbumBreaks", breaks);
            intent.putExtra("newAlbumKey", bytes);
            //This is necessary
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            JSONObject jsAlbum = new JSONObject(res);

            String songs = "";

            JSONArray jsSongs = jsAlbum.getJSONArray("Songs");
            for (int i = 0; i < jsSongs.length(); i++) {
                songs += (String) jsSongs.get(i) + ",";
            }

            //TODO: THIS IS BAD PRACTICE YOU SHOULD BE ASHAMED!
            songs += "REMOVETHIS";

            songs = songs.replace(",REMOVETHIS", "");

            byte[] decodedString = Base64.decode(jsAlbum.getString("Image"), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            String albumName = jsAlbum.getString("Name");
            String artistName = jsAlbum.getString("Artist");

            ((MyGlobalVariables) getApplication()).CurrentBitmap = decodedByte;

            goToCurrentSongListScreen(songs, artistName, albumName, bytes);
        }
    }

    //Create an HTTP GET Request to get all Songs associated with the album
    //TODO:LOOKS LIKE WE AREN'T USING THIS
    private void getAlbumSongs(byte[] by) throws IOException {
        byte[] bytes = {(byte) 0x54, (byte) 0x54};
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        //Form the query String
        String ip = ((MyGlobalVariables) getApplication()).DatabaseIp.toString();
        String query = "http://" + ip + "/api/Song?s=" + sb;
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

    //Create an HTTP POST Request with album and song data from a new album
    //Image is previously added to globals
    private void addAlbumData(byte[] key, String songs, String artist, String album, String image) throws IOException, URISyntaxException, JSONException {

        StringBuilder sb = new StringBuilder();
        for (byte b : key) {
            sb.append(String.format("%02X", b));
        }

        //Form the query String
        String ip = ((MyGlobalVariables) getApplication()).DatabaseIp.toString();
        String query = "http:/" + ip + "/api/Song";
        HttpResponse response = null;
        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(query);

        JSONObject Song = new JSONObject();
        Song.put("Key", sb);
        Song.put("Songs", songs);
        Song.put("Artist", artist);
        Song.put("Album", album);
        Song.put("Image", image);

        StringEntity entity = new StringEntity(Song.toString(), HTTP.UTF_8);
        entity.setContentType("application/json");

        request.setEntity(entity);

        //Execute the request
        response = client.execute(request);

        //Bring up the CurrentListScreen
        goToCurrentSongListScreen(songs, artist, album, key);
    }

    private void goToCurrentSongListScreen(String songs, String artist, String album, byte[] key) {
        Intent intent = new Intent(this, CurrentListScreen.class);
        intent.putExtra("type","normal");
        intent.putExtra("songs", songs);
        intent.putExtra("album", album);
        intent.putExtra("artist", artist);
        intent.putExtra("key", key);

        ((MyGlobalVariables) getApplication()).CurrentAlbum = album;
        ((MyGlobalVariables) getApplication()).CurrentArtist = artist;
        ((MyGlobalVariables) getApplication()).CurrentKey = key;
        ((MyGlobalVariables) getApplication()).CurrentSong = null;
        ((MyGlobalVariables) getApplication()).CurrentSongList = new ArrayList<String>(Arrays.asList(songs.split(",")));
        ((MyGlobalVariables) getApplication()).HasAlbum = true;

        //This is necessary
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void goToTotalArtistScreen() {
        Intent intent = new Intent(this, TotalArtistScreen.class);
        //This is necessary
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void goToTotalAlbumScreen() {
        Intent intent = new Intent(this, TotalAlbumScreen.class);
        //This is necessary
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void goToTotalSongScreen(String artist, String album, ArrayList<String> songs) {
        Intent intent = new Intent(this, TotalSongScreen.class);
        intent.putExtra("Artist", artist);
        intent.putExtra("Album", album);
        intent.putExtra("Songs", songs);
        //This is necessary
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
