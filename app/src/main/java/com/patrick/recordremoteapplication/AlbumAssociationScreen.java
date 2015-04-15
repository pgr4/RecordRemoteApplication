package com.patrick.recordremoteapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;


public class AlbumAssociationScreen extends ActionBarActivity {
    private ListView mainListView;
    private ArrayAdapter<LastFmAlbum> listAdapter;
    private LastFmAlbum SelectedAlbum = null;
    private int[] key;
    private int breaks;
    private String artistName;
    private Bitmap artistBitmap;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_association_screen);

        //Grab the NewAlbum from the intent
        Bundle b = getIntent().getExtras();

        key = b.getIntArray("newAlbumKey");
        breaks = b.getInt("newAlbumBreaks");
        artistName = b.getString("artistName");
        //artistBitmap = b.getParcelable("artistBitmap");

        //Get the List
        mainListView = (ListView) findViewById(R.id.albumAssociationList);
        progressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);

        //Get and fill the List with albums from the online database matching the artist selected
        getAlbums(artistName);

        //Set up the item on click event
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                view.setSelected(true);
                SelectedAlbum = (LastFmAlbum) adapter.getAdapter().getItem(position);
                goToSongAssociationScreen(SelectedAlbum.AlbumName, SelectedAlbum.ImageUrl);
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

        new LastFmAlbumLookup(this) {

            @Override
            protected void onPostExecute(ArrayList<LastFmAlbum> result) {

                progressBar.setVisibility(View.GONE);
                setAdapter(result);
                
            }

        }.execute(artistName);
    }

    private void setAdapter(ArrayList<LastFmAlbum> lst) {
        AlbumListAdapter adapter = new AlbumListAdapter(this, lst);
        mainListView.setAdapter(adapter);
    }

    private void goToSongAssociationScreen(String albumName, String imgUrl) {
        Intent intent = new Intent(this, SongAssociationScreen.class);
        intent.putExtra("newAlbumBreaks", breaks);
        intent.putExtra("newAlbumKey", key);
        intent.putExtra("artistName", artistName);
        intent.putExtra("albumName", albumName);
        intent.putExtra("albumImgUrl", imgUrl);
        startActivity(intent);
    }
}
