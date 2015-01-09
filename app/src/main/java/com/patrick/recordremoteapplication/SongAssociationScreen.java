package com.patrick.recordremoteapplication;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class SongAssociationScreen extends ActionBarActivity {
    private ListView mainListView;
    private ArrayAdapter<String> listAdapter;
    private String SelectedSong;
    private byte[] key;
    private int breaks;
    private String artistName;
    private String albumName;

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

        //Get the ListView
        mainListView = (ListView) findViewById(R.id.songAssociationList);

        //Get the Songs from the online database
        getSongs();

        //Set up the item on click event
        //TODO: Bring up a popup to delete/merge
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_album_association_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            //TODO: Check if everything is ok. Then show CurrentListScreen with Updated information
            //handleAccept();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getSongs(){
        //GET START TIME
        Log.d("getAlbumsTiming", String.valueOf(System.currentTimeMillis()));

        new LastFmSongLookup() {

            @Override
            protected void onPostExecute(ArrayList<String> result) {
                //GET START TIME
                Log.d("getAlbumsTiming",  String.valueOf(System.currentTimeMillis()));

                setAdapter(result);

                //GET START TIME
                Log.d("getArtistsTiming",  String.valueOf(System.currentTimeMillis()));
            }

        }.execute(artistName, albumName);
    }

    private void setAdapter(ArrayList<String> lst) {
        SongListAdapter adapter = new SongListAdapter(this, lst);
        mainListView.setAdapter(adapter);
    }

    private boolean checkConditions() {
        return true;
    }

    private void goToCurrentSongListScreen() {
        Intent intent = new Intent(this, CurrentListScreen.class);
        intent.putExtra("newAlbumKey", key);
        startActivity(intent);
    }
}
