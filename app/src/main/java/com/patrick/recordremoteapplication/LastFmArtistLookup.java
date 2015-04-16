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
import java.util.ArrayList;
import java.net.URLEncoder;
import java.util.Collections;

/**
 * Created by pat on 12/30/2014.
 */

public class LastFmArtistLookup extends AsyncTask<String, Integer, ArrayList<LastFmArtist>> {

    private ListView lv;

    public LastFmArtistLookup(Activity activity) {
        lv = (ListView) activity.findViewById(R.id.artistAssociationList);
    }

    protected ArrayList doInBackground(String... strings) {
        final ArrayList<LastFmArtist> artistList = new ArrayList<LastFmArtist>();

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
            //Create the JSONObject from the string
            JSONObject jso = new JSONObject(LastFmBaseLookup.ConvertStreamToString(s));
            //Get the "artistmatches" array in our object
            JSONObject artists = LastFmBaseLookup.getFoundResultCount(jso.toJSONArray(jso.names()), "Artist");
            //Get the Json Artists
            JSONArray lst = artists.getJSONArray("artist");
            for (int i = 0; i < lst.length(); i++) {
                JSONObject obj = lst.getJSONObject(i);
                String imageUrl = "http://s3.amazonaws.com/t11wire/media/img/artist-default.jpg";
                JSONArray images = obj.getJSONArray("image");
                if (images.length() > 0) {
                    JSONObject imgObj = images.getJSONObject(images.length() - 1);
                    imageUrl = imgObj.getString("#text");
                }

                new BitmapRetriever() {

                    @Override
                    protected void onPostExecute(BitmapWithNumber result) {
                        if (result != null) {
                            for (int j = 0; j < artistList.size(); j++) {
                                try {
                                    LastFmArtist lastFmArtist = artistList.get(j);
                                    if (lastFmArtist.Order == result.Order) {
                                        lastFmArtist.Bitmap = result.BitmapImage;
                                        View view = lv.getChildAt(lastFmArtist.PostOrder);
                                        if (view != null) {
                                            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
                                            imageView.setImageBitmap(result.BitmapImage);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                }.execute(imageUrl, String.valueOf(i));

                artistList.add(new LastFmArtist(obj.getString("name"), null, i));
            }

            Collections.sort(artistList, LastFmArtist.StuNameComparator);

            for (int i = 0; i < artistList.size(); i++) {
                artistList.get(i).PostOrder = i;
            }

        } catch (URISyntaxException | IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            return artistList;
        }
    }

}
