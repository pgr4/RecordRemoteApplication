package com.patrick.recordremoteapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;


public class ArtistAssociationScreen extends ActionBarActivity {
    private ListView mainListView;
    private LastFmArtist SelectedArtist;
    private int[] key;
    private int breaks;
    private EditText editTextArtist;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_association_screen);

        //Grab the NewAlbum from the intent
        Bundle b = getIntent().getExtras();
        key = b.getIntArray("newAlbumKey");
        breaks = b.getInt("newAlbumBreaks");

        //Get the controls
        editTextArtist = (EditText) findViewById(R.id.artistText);
        mainListView = (ListView) findViewById(R.id.artistAssociationList);
        progressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);

        //Set up the item on click event
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

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextArtist.getWindowToken(), 0);

        progressBar.setVisibility(View.VISIBLE);

        new LastFmArtistLookup(this) {

            @Override
            protected void onPostExecute(ArrayList<LastFmArtist> result) {

                setAdapter(result);

            }

        }.execute(editTextArtist.getText().toString());
    }

    public void setAdapter(ArrayList<LastFmArtist> lst) {
        progressBar.setVisibility(View.GONE);
        mainListView.setVisibility(View.VISIBLE);
        ArtistListAdapter adapter = new ArtistListAdapter(this, lst);
        mainListView.setAdapter(adapter);
    }

}
