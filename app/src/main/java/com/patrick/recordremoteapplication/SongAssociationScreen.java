package com.patrick.recordremoteapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class SongAssociationScreen extends ActionBarActivity {
    private ListView mainListView;
    private ArrayAdapter<String> listAdapter;
    private String SelectedSong;
    private byte[] key;
    private int breaks;
    private String artistName;
    private String albumName;
    private String albumImageUrl;
    SongListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_association_screen);

        //Grab the NewAlbum from the intent
        Bundle b = getIntent().getExtras();

        key = b.getByteArray("newAlbumKey");
        breaks = b.getInt("newAlbumBreaks");
        albumName = b.getString("albumName");
        artistName = b.getString("artistName");
        albumImageUrl = b.getString("albumImgUrl");

        //Get the ListView
        mainListView = (ListView) findViewById(R.id.songAssociationList);

        //Get the Songs from the online database
        getSongs();

        registerForContextMenu(mainListView);

        //Set up the item on click event
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                view.setSelected(true);
                SelectedSong = (String) adapter.getAdapter().getItem(position);

//                if (checkConditions()) {
//                    goToCurrentSongListScreen();
//                } else {
//
//                }
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.songAssociationList) {
            int w = adapter.getCount();
            if (adapter.getCount() > breaks + 1) {
                ListView lv = (ListView) v;
                AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
                String obj = (String) lv.getItemAtPosition(acmi.position);

                menu.add("Delete");
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Delete") {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            adapter.remove(adapter.getItem(info.position));
            mainListView.setAdapter(adapter);
        } else {
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_album_association_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getSongs() {

        new LastFmSongLookup() {

            @Override
            protected void onPostExecute(ArrayList<String> result) {

                setAdapter(result);

            }

        }.execute(artistName, albumName);
    }

    private void setAdapter(ArrayList<String> lst) {
        while (lst.size() < breaks + 1) {
            lst.add("Title " + String.valueOf(lst.size() + 1));
        }

        adapter = new SongListAdapter(this, lst);
        mainListView.setAdapter(adapter);
    }

    private boolean checkConditions() {
        return breaks + 1 == adapter.getCount();
    }

    //Create the Database Service
    //Create the Sync
    public void onAccept(View view) {
        if (checkConditions()) {
            ((MyGlobalVariables) this.getApplication()).CurrentAlbum = albumName;
            ((MyGlobalVariables) this.getApplication()).CurrentArtist = artistName;
            ((MyGlobalVariables) this.getApplication()).CurrentBitmap = getBitmapFromURL(albumImageUrl);
            ((MyGlobalVariables) this.getApplication()).HasAlbum = true;

            Intent intent = new Intent(this, DatabaseService.class);
            intent.putExtra("type", "addAlbumData");
            intent.putExtra("key", key);
            intent.putExtra("songs", adapter.csSongs);
            intent.putExtra("album", albumName);
            intent.putExtra("artist", artistName);
            intent.putExtra("image", albumImageUrl);
            startService(intent);

            Intent dIntent = new Intent(this, SenderService.class);
            dIntent.putExtra("type", "sync");
            dIntent.putExtra("key", key);
            startService(dIntent);
        }
    }

    private Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            return null;
        }
    }
}
