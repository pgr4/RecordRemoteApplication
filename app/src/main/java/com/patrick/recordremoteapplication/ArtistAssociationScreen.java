package com.patrick.recordremoteapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;


public class ArtistAssociationScreen extends ActionBarActivity {
    private ListView mainListView;
    private ArrayAdapter<Song> listAdapter;
    private Song SelectedSong = null;
    private byte[] key;
    private int breaks;
    private ArrayList<LastFmArtist> lst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_association_screen);

        //Grab the NewAlbum from the intent
        //Go through database to fill in the view.
        Bundle b = getIntent().getExtras();

        key = b.getByteArray("newAlbumKey");
        breaks = b.getInt("newAlbumBreaks");

        //Get the List
        mainListView = (ListView) findViewById(R.id.artistAssociationList);

        //Set up the item on click event
        //TODO: Bring up a popup to delete/merge
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                Song selItem = (Song) adapter.getAdapter().getItem(position);
                SelectedSong = selItem;
                view.setSelected(true);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_artist_association_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getArtists(View view) throws IOException {
        EditText et = (EditText) findViewById(R.id.artistText);
        new LastFmLookup() {

            @Override
            protected void onPostExecute(ArrayList<LastFmArtist> result) {
                String[] names = new String[result.size()];
                Bitmap[] images = new Bitmap[result.size()];

                //Split the ArrayList into two separate arrays
                for (int i = 0; i < result.size(); i++) {
                    names[i] = result.get(i).Name;
                    images[i] = result.get(i).Bitmap;
                }

                setAdapter(names, images);
            }

        }.execute("Artist", et.getText().toString());
    }

    public void setAdapter(String[] a, Bitmap[] b) {
        ImageStringListAdapter adapter = new ImageStringListAdapter(this, a, b);
        mainListView.setAdapter(adapter);
    }

    public void goToAlbumAssociationScreen(View view) {
        TextView tv = (TextView) findViewById(R.id.artistText);
        Intent intent = new Intent(this, AlbumAssociationScreen.class);
        intent.putExtra("newAlbumBreaks", breaks);
        intent.putExtra("newAlbumKey", key);
        intent.putExtra("Album", tv.getText().toString());
        startActivity(intent);
    }
}
