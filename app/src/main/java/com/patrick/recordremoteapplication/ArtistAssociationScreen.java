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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;


public class ArtistAssociationScreen extends ActionBarActivity {
    private ListView mainListView;
    private LastFmArtist SelectedArtist;
    private byte[] key;
    private int breaks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_association_screen);

        //Grab the NewAlbum from the intent
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
                view.setSelected(true);
                SelectedArtist = (LastFmArtist) adapter.getAdapter().getItem(position);
                TextView tv = (TextView) findViewById(R.id.artistText);
                tv.setText(SelectedArtist.Name.toString());

                goToAlbumAssociationScreen(SelectedArtist.Name.toString(), SelectedArtist.Bitmap);
            }
        });

    }

    private void goToAlbumAssociationScreen(String artistName, Bitmap bitmap) {
        Intent intent = new Intent(this, AlbumAssociationScreen.class);
        intent.putExtra("newAlbumBreaks", breaks);
        intent.putExtra("newAlbumKey", key);
        intent.putExtra("artistName", artistName);
        //intent.putExtra("artistBitmap",bitmap);
        startActivity(intent);
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

        new LastFmArtistLookup() {

            @Override
            protected void onPostExecute(ArrayList<LastFmArtist> result) {
                //GET START TIME
                Log.d("getArtistsTiming", String.valueOf(System.currentTimeMillis()));

                setAdapter(result);

                //GET START TIME
                Log.d("getArtistsTiming", String.valueOf(System.currentTimeMillis()));
            }

        }.execute(et.getText().toString());
    }

    public void setAdapter(ArrayList<LastFmArtist> lst) {
        ArtistListAdapter adapter = new ArtistListAdapter(this, lst);
        mainListView.setAdapter(adapter);
    }

}
