package com.patrick.recordremoteapplication;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;


public class ManualAssociationScreen extends ActionBarActivity {
    private ListView mainListView;
    private int[] key;
    private int breaks;
    private String SelectedSong;
    EditableSongListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_association_screen);
        //Grab the NewAlbum from the intent
        Bundle b = getIntent().getExtras();

        key = b.getIntArray("newAlbumKey");
        breaks = b.getInt("newAlbumBreaks");

        //Get the ListView
        mainListView = (ListView) findViewById(R.id.songAssociationList);
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < breaks; i++) {
            arrayList.add("Title " + i);
        }

        adapter = new EditableSongListAdapter(this, arrayList);
        mainListView.setAdapter(adapter);

        registerForContextMenu(mainListView);

        //Set up the item on click event
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                view.setSelected(true);
                SelectedSong = (String) adapter.getAdapter().getItem(position);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manual_association_screen, menu);
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

    boolean checkConditions() {
        String albumName = ((EditText) findViewById(R.id.albumEditText)).getText().toString();
        String artistName = ((EditText) findViewById(R.id.artistEditText)).getText().toString();
        if (albumName.isEmpty() || artistName.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    //Create the Database Service
    //Create the Sync
    public void onAccept(View view) {
        if (checkConditions()) {
            String albumName = ((EditText) findViewById(R.id.albumEditText)).getText().toString();
            String artistName = ((EditText) findViewById(R.id.artistEditText)).getText().toString();

            ((MyGlobalVariables) this.getApplication()).CurrentAlbum = albumName;
            ((MyGlobalVariables) this.getApplication()).CurrentArtist = artistName;
            ((MyGlobalVariables) this.getApplication()).CurrentBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.vinylrecord);

            ((MyGlobalVariables) this.getApplication()).HasAlbum = true;

            Intent intent = new Intent(this, DatabaseService.class);
            intent.putExtra("type", "addAlbumDataSans");
            intent.putExtra("key", key);
            intent.putExtra("songs", adapter.csSongs);
            intent.putExtra("album", albumName);
            intent.putExtra("artist", artistName);
            startService(intent);

            Intent dIntent = new Intent(this, SenderService.class);
            dIntent.putExtra("type", "sync");
            dIntent.putExtra("key", key);
            startService(dIntent);

            finish();
        }
    }
}
