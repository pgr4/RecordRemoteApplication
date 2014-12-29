package com.patrick.recordremoteapplication;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class SongAssociationScreen extends ActionBarActivity {
    private ListView mainListView;
    private ArrayAdapter<Song> listAdapter;
    private Song SelectedSong = null;
    private byte[] key;
    private int breaks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_association_screen);

        //Grab the NewAlbum from the intent
        //Go through database to fill in the view.
        Bundle b = getIntent().getExtras();

        key = b.getByteArray("newAlbumKey");
        breaks = b.getInt("newAlbumBreaks");

        //Use key to get the album and song information

        //Get the List
        mainListView = (ListView) findViewById( R.id.songAssociationList );

        //Initialize the songList
        ArrayList<Song> songList = new ArrayList<Song>();

        //Fill the list
        for(int i = 0; i< breaks; i++) {
            songList.add(new Song("Song " + i,i));
        }

        //Use the list_Item to display the song info
        listAdapter = new ArrayAdapter<Song>(this, R.layout.editable_list_item, songList);

        //Set the list's adapter
        mainListView.setAdapter( listAdapter );

        //Set up the item on click event
        //TODO: Bring up a popup to delete/merge
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                Song selItem = (Song)adapter.getAdapter().getItem(position);
                SelectedSong = selItem;
                view.setSelected(true);
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
            handleAccept();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Do a check to make sure information is valid
    //If it is create an intent to show the CurrentListScreen
    //Else show a popup of what went wrong
    private void handleAccept(){
        Intent intent = new Intent(this, CurrentListScreen.class);
        intent.putExtra("newAlbumBreaks", key);
        startActivity(intent);
    }
}
