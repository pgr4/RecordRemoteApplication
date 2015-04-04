package com.patrick.recordremoteapplication;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

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

/**
 * Created by pat on 12/31/2014.
 */
public class LastFmAlbumLookup extends AsyncTask<String, Void, ArrayList<LastFmAlbum>> {

    ListView lv;

    public LastFmAlbumLookup(Activity activity){
        lv = (ListView) activity.findViewById(R.id.albumAssociationList);
    }

    protected ArrayList doInBackground(String... strings) {

        final ArrayList<LastFmAlbum> albumList = new ArrayList<>();

        try {
            //Form the query String
            String query = LastFmBaseLookup.StartQuery + LastFmBaseLookup.albumQuery + URLEncoder.encode(strings[0], "UTF-8") + LastFmBaseLookup.EndQuery;
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
            //Create the JSONObject from the string
            JSONObject jso = new JSONObject(LastFmBaseLookup.ConvertStreamToString(s));
            //Create a JSONArray from the names (This should only get us one object!)
            JSONArray jsonArray = jso.toJSONArray(jso.names());
            //Get the "artistmatches" array in our object
            JSONArray lst = jsonArray.getJSONObject(0).getJSONArray("album");

            for (int i = 0; i < lst.length(); i++) {
                JSONObject obj = lst.getJSONObject(i);
                String imageUrl = "http://thumbs.dreamstime.com/x/vinyl-record-green-label-7227421.jpg";
                JSONArray images = obj.getJSONArray("image");
                if (images.length() > 0) {
                    JSONObject imgObj = images.getJSONObject(images.length() - 1);
                    imageUrl = imgObj.getString("#text");
                }

                new BitmapRetriever() {

                    @Override
                    protected void onPostExecute(BitmapWithNumber result) {
                        for (int j = 0; j < albumList.size(); j++) {
                            LastFmAlbum lastFmAlbum = albumList.get(j);
                            if (lastFmAlbum.Order == result.Order) {
                                lastFmAlbum.Bitmap = result.BitmapImage;
                                try {
                                    View view = lv.getChildAt(lastFmAlbum.PostOrder);
                                    ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
                                    imageView.setImageBitmap(result.BitmapImage);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                }.execute(imageUrl, String.valueOf(i));

                albumList.add(new LastFmAlbum(obj.getString("name"), strings[0], null, imageUrl, i));
            }

            Collections.sort(albumList, LastFmAlbum.StuNameComparator);

            for(int i = 0;i<albumList.size();i++){
                albumList.get(i).PostOrder = i;
            }

            return albumList;
        } catch (URISyntaxException | IOException | JSONException e) {
            e.printStackTrace();
        }
        return albumList;
    }

}
