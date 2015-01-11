package com.patrick.recordremoteapplication;

import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;


public class CurrentListScreen extends ActionBarActivity {
    private ImageView imgAlbumArt;
    private ImageButton imgbtnPlay;
    private ImageButton imgbtnSkip;
    private ImageButton imgbtnBack;
    private ListView mainListView;
    private ArrayAdapter<String> listAdapter;
    private TextView ArtistText;
    private TextView AlbumText;
    private TextView SongText;
    private String artistName;
    private String albumName;
    private String[] arrSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_list_screen);

        //Grab the NewAlbum from the intent
        //Go through database to fill in the view.
        Bundle b = getIntent().getExtras();

        arrSongs = b.getString("songs").split(",");
        artistName = b.getString("artist");
        albumName = b.getString("album");

        //Get the List
        mainListView = (ListView) findViewById(R.id.currentListView);

        //Initialize the songList
        ArrayList<String> songList = new ArrayList<>();

        //Fill the list
        for (int i = 0; i < arrSongs.length; i++) {
            songList.add(arrSongs[i]);
        }

        //Setup the adapter
        SongListAdapter adapter = new SongListAdapter(this, songList);
        mainListView.setAdapter(adapter);

        //Set up the item on click event
        //TODO: Bring up a popup to play or.....
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                view.setSelected(true);
            }
        });

        //Set the Album Text
        AlbumText = (TextView) findViewById(R.id.albumText);
        AlbumText.setText(albumName);

        //Set the Artist Text
        ArtistText = (TextView) findViewById(R.id.artistText);
        ArtistText.setText(artistName);

        //Set the Song Text (SHOW AS EMPTY)
        SongText = (TextView) findViewById(R.id.songText);
        SongText.setText("");

        //Set the album art
        imgAlbumArt = (ImageView) findViewById(R.id.imgAlbumArt);
        imgAlbumArt.setImageBitmap((Bitmap) b.getParcelable("image"));

        /* Media Control Images */
        imgbtnPlay = (ImageButton) findViewById(R.id.imgbtnPlay);
        imgbtnPlay.setImageResource(R.drawable.ic_action_play);

        imgbtnSkip = (ImageButton) findViewById(R.id.imgbtnSkip);
        imgbtnSkip.setImageResource(R.drawable.ic_action_fast_forward);

        imgbtnBack = (ImageButton) findViewById(R.id.imgbtnBack);
        imgbtnBack.setImageResource(R.drawable.ic_action_rewind);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_current_list_screen, menu);
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


}
