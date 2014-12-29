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
import android.widget.TextView;

import java.util.ArrayList;


public class ArtistAssociationScreen extends ActionBarActivity {
    private ListView mainListView;
    private ArrayAdapter<Song> listAdapter;
    private Song SelectedSong = null;
    private byte[] key;
    private int breaks;

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
        mainListView = (ListView) findViewById( R.id.artistAssociationList );

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

    public void getArtists(View view){
        //Initialize the songList
        ArrayList<Song> songList = new ArrayList<Song>();

        //Fill the list
        for(int i = 0; i< breaks; i++) {
            songList.add(new Song("Song " + i,i));
        }

        //Use the list_Item to display the song info
        listAdapter = new ArrayAdapter<Song>(this, R.layout.list_item, songList);

        //Set the list's adapter
        mainListView.setAdapter( listAdapter );
    }

    public void goToAlbumAssociationScreen(View view){
        TextView tv = (TextView)findViewById( R.id.artistText );
        Intent intent = new Intent(this, AlbumAssociationScreen.class);
        intent.putExtra("newAlbumBreaks", breaks);
        intent.putExtra("newAlbumKey", key);
        intent.putExtra("Album", tv.getText().toString());
        startActivity(intent);
    }
}
