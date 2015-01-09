package com.patrick.recordremoteapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class AlbumAssociationScreen extends ActionBarActivity {
    private ListView mainListView;
    private ArrayAdapter<LastFmAlbum> listAdapter;
    private LastFmAlbum SelectedAlbum = null;
    private byte[] key;
    private int breaks;
    private String artistName;
    private Bitmap artistBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_association_screen);

        //Grab the NewAlbum from the intent
        Bundle b = getIntent().getExtras();

        key = b.getByteArray("newAlbumKey");
        breaks = b.getInt("newAlbumBreaks");
        artistName = b.getString("artistName");
        //artistBitmap = b.getParcelable("artistBitmap");

        //Get the List
        mainListView = (ListView) findViewById(R.id.albumAssociationList);

        //Get and fill the List with albums from the online database matching the artist selected
        getAlbums(artistName);

        //Set up the item on click event
        //TODO: Bring up a popup to delete/merge
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                view.setSelected(true);
                SelectedAlbum = (LastFmAlbum) adapter.getAdapter().getItem(position);
                //TextView tv = (TextView) findViewById(R.id.artistText);
                //tv.setText(SelectedAlbum.AlbumName.toString());
                goToSongAssociationScreen(SelectedAlbum.AlbumName, SelectedAlbum.Bitmap);
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

    //Get the album list from the online database
    private void getAlbums(String artistName) {
        //GET START TIME
        Log.d("getAlbumsTiming", String.valueOf(System.currentTimeMillis()));

        new LastFmAlbumLookup() {

            @Override
            protected void onPostExecute(ArrayList<LastFmAlbum> result) {
                //GET START TIME
                Log.d("getAlbumsTiming",  String.valueOf(System.currentTimeMillis()));

                setAdapter(result);

                //GET START TIME
                Log.d("getArtistsTiming",  String.valueOf(System.currentTimeMillis()));
            }

        }.execute(artistName);
    }

    private void setAdapter(ArrayList<LastFmAlbum> lst) {
        AlbumListAdapter adapter = new AlbumListAdapter(this, lst);
        mainListView.setAdapter(adapter);
    }

    private void goToSongAssociationScreen(String albumName, Bitmap bitmap){
        Intent intent = new Intent(this, SongAssociationScreen.class);
        intent.putExtra("newAlbumBreaks", breaks);
        intent.putExtra("newAlbumKey", key);
        intent.putExtra("artistName", artistName);
        intent.putExtra("albumName", albumName);
        //intent.putExtra("artistBitmap",bitmap);
        startActivity(intent);
    }
}
