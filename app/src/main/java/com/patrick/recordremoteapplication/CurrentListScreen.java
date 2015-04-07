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
    private boolean isPlaying;
    private ImageButton imgbtnPause;
    private String currentSong;

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver), new IntentFilter("currentListScreen"));
        isPlaying = ((MyGlobalVariables) this.getApplication()).IsPlaying;
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }

    @Override
    protected void onResume(){
        super.onResume();
        isPlaying = ((MyGlobalVariables) this.getApplication()).IsPlaying;
        currentSong = ((MyGlobalVariables) this.getApplication()).CurrentSong;
        SetCurrentSong(currentSong);
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
            }

        });

        isPlaying = ((MyGlobalVariables) this.getApplication()).IsPlaying;

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
        ImageButton imgbtnPlay = (ImageButton) findViewById(R.id.imgbtnPlay);
        imgbtnPlay.setImageResource(R.drawable.ic_action_play);

        ImageButton imgbtnSkip = (ImageButton) findViewById(R.id.imgbtnSkip);
        imgbtnSkip.setImageResource(R.drawable.ic_action_fast_forward);

        ImageButton imgbtnBack = (ImageButton) findViewById(R.id.imgbtnBack);
        imgbtnBack.setImageResource(R.drawable.ic_action_rewind);

        imgbtnPause = (ImageButton) findViewById(R.id.imgbtnPause);
        imgbtnPause.setImageResource(R.drawable.ic_action_pause);
        imgbtnPause.setEnabled(false);

        receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String type = intent.getStringExtra("type");
                if (type == "beginning") {
                    SongText.setText(adapter.getItem(0));
                    currentIndex = 0;
                    imgbtnPause.setEnabled(true);
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
                    imgbtnPause.setEnabled(true);
                } else if (type == "isPlaying") {
                    isPlaying = intent.getBooleanExtra("value", false);
                    imgbtnPause.setEnabled(false);
                }
            }
        };
    }

    private void SetCurrentSong(String song){
        SongText.setText(song);
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

    //Sends a playBeginning command if it isn't the first song
    //Sends a play command otherwise
    public void Play(View view) {
        if (selectedIndex != -1) {
            if (currentIndex == selectedIndex && !isPlaying) {
                Intent intent = new Intent(this, SenderService.class);
                intent.putExtra("type", "play");
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
                    intent.putExtra("type", "playTrack");
                    intent.putExtra("location", key[currentIndex]);
                    startService(intent);
                }
            }
        }
    }

    public void Pause(View view) {
        Intent intent = new Intent(this, SenderService.class);
        intent.putExtra("type", "pause");
        startService(intent);
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
