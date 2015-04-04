package com.patrick.recordremoteapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
    private ImageButton imgbtnPlayPause;
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
    private byte[] key;
    private BroadcastReceiver receiver;
    private SongListAdapter adapter;
    private int selectedIndex = -1;
    private int currentIndex = -1;

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver), new IntentFilter("currentListScreen"));
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }

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
        key = b.getByteArray("key");

        //Get the List
        mainListView = (ListView) findViewById(R.id.currentListView);

        //Initialize the songList
        ArrayList<String> songList = new ArrayList<>();

        //Fill the list
        for (String arrSong : arrSongs) {
            songList.add(arrSong);
        }

        //Setup the adapter
        adapter = new SongListAdapter(this, songList);
        mainListView.setAdapter(adapter);

        //Set up the item on click event
        //TODO: Bring up a popup to play or.....
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                view.setSelected(true);
                selectedIndex = position;

                if (currentIndex == selectedIndex) {
                    imgbtnPlayPause.setImageResource(R.drawable.ic_action_pause);
                } else {
                    imgbtnPlayPause.setImageResource(R.drawable.ic_action_play);
                }
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
        imgAlbumArt.setImageBitmap(((MyGlobalVariables) this.getApplication()).CurrentBitmap);

        /* Media Control Images */
        imgbtnPlayPause = (ImageButton) findViewById(R.id.imgbtnPlayPause);
        imgbtnPlayPause.setImageResource(R.drawable.ic_action_play);

        imgbtnSkip = (ImageButton) findViewById(R.id.imgbtnSkip);
        imgbtnSkip.setImageResource(R.drawable.ic_action_fast_forward);

        imgbtnBack = (ImageButton) findViewById(R.id.imgbtnBack);
        imgbtnBack.setImageResource(R.drawable.ic_action_rewind);

        receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String type = intent.getStringExtra("type");
                if (type == "beginning") {
                    SongText.setText(adapter.getItem(0));
                    currentIndex = 0;
                } else if (type == "location") {
                    Byte defaultByte = 0;
                    Byte location = intent.getByteExtra("location", defaultByte);
                    for (int i = 0; i < key.length; i++) {
                        if (key[i] == location) {
                            SongText.setText(adapter.getItem(i + 1));
                            currentIndex = i + 1;
                            break;
                        }
                    }
                }
            }
        };
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    public void PlayPause(View view) {
        if (selectedIndex != -1) {
            if (currentIndex == selectedIndex) {
                //Send pause
                Intent intent = new Intent(this, SenderService.class);
                intent.putExtra("type", "pause");
                startService(intent);
            } else {
                //Send play for selectedIndex
                if (selectedIndex == 0) {
                    Intent intent = new Intent(this, SenderService.class);
                    intent.putExtra("type", "playBeginning");
                    startService(intent);
                } else {
                    //Send play for first song
                    Intent intent = new Intent(this, SenderService.class);
                    intent.putExtra("type", "play");
                    intent.putExtra("location", key[currentIndex]);
                    startService(intent);
                }
            }
        }
    }

    public void Skip(View view) {
        if (currentIndex != -1) {
            if (currentIndex == adapter.getCount() - 1) {
                //Send play for first song
                Intent intent = new Intent(this, SenderService.class);
                intent.putExtra("type", "playBeginning");
                startService(intent);
            } else {
                //Send play for first song
                Intent intent = new Intent(this, SenderService.class);
                intent.putExtra("type", "play");
                intent.putExtra("location", key[currentIndex + 1]);
                startService(intent);
            }
        }
    }

    public void Back(View view) {
        if (currentIndex != -1) {
            if (currentIndex == 0) {
                //Send play for first song
                Intent intent = new Intent(this, SenderService.class);
                intent.putExtra("type", "playBeginning");
                startService(intent);
            } else {
                //Send play for first song
                Intent intent = new Intent(this, SenderService.class);
                intent.putExtra("type", "play");
                intent.putExtra("location", key[currentIndex - 1]);
                startService(intent);
            }
        }
    }

}
