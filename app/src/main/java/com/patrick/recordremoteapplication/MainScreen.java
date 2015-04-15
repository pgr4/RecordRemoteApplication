package com.patrick.recordremoteapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class MainScreen extends ActionBarActivity {
    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private TextView tvCurrentPlayingArtist;
    private TextView tvCurrentPlayingAlbum;
    private ImageView ivCurrentAlbumArt;
    private BroadcastReceiver receiver;
    private LinearLayout currentLinearLayout;
    private TextView CurrentSongTextView;
    private TextView textViewBusy;
    private Switch powerSwitch;

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver), new IntentFilter("mainScreen"));
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Reset Current Album

        if (((MyGlobalVariables) this.getApplication()).HasAlbum) {
            currentLinearLayout.setVisibility(View.VISIBLE);
            ivCurrentAlbumArt.setImageBitmap(((MyGlobalVariables) this.getApplication()).CurrentBitmap);
            tvCurrentPlayingArtist.setText(((MyGlobalVariables) this.getApplication()).CurrentArtist);
            tvCurrentPlayingAlbum.setText(((MyGlobalVariables) this.getApplication()).CurrentAlbum);
            CurrentSongTextView.setText(((MyGlobalVariables) this.getApplication()).CurrentSong);
        } else {
            currentLinearLayout.setVisibility(View.INVISIBLE);
        }


        //Reset Status

        switch (((MyGlobalVariables) this.getApplication()).Status) {
            case Ready:
                findViewById(R.id.circle).setBackground(getResources().getDrawable(R.drawable.green_circle));
                break;
            case Pause:
            case Play:
            case GoToTrack:
            case Scan:
            case Stop:
                findViewById(R.id.circle).setBackground(getResources().getDrawable(R.drawable.red_circle));
                break;
            case Unknown:
                findViewById(R.id.circle).setBackground(getResources().getDrawable(R.drawable.orange_circle));
                break;
        }

        textViewBusy.setText(((MyGlobalVariables) this.getApplication()).Status.getString());

        //Reset Power

        powerSwitch.setChecked(((MyGlobalVariables) this.getApplication()).IsPowerOn);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        //Get the IP address
        try {
            ((MyGlobalVariables) this.getApplication()).MyIp = InetAddress.getByName(Utils.getIPAddress(true));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        mNavigationDrawerItemTitles = getResources().getStringArray(R.array.drawerActivities);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.drawer_item, mNavigationDrawerItemTitles);
        mDrawerList.setAdapter(adapter);

        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        powerSwitch = (Switch) findViewById(R.id.swPower);
        textViewBusy = (TextView) findViewById(R.id.tvBusyStatusValue);
        textViewBusy.setText(((MyGlobalVariables) this.getApplication()).Status.getString());
        currentLinearLayout = (LinearLayout) findViewById(R.id.llCurrent);

        tvCurrentPlayingAlbum = (TextView) findViewById(R.id.tvCurrentPlayingAlbum);
        tvCurrentPlayingArtist = (TextView) findViewById(R.id.tvCurrentPlayingArtist);
        ivCurrentAlbumArt = (ImageView) findViewById(R.id.ivCurrentAlbumArt);
        CurrentSongTextView = (TextView) findViewById(R.id.CurrentSongTextView);

        if (((MyGlobalVariables) this.getApplication()).HasAlbum) {
            currentLinearLayout.setVisibility(View.VISIBLE);
            ivCurrentAlbumArt.setImageBitmap(((MyGlobalVariables) this.getApplication()).CurrentBitmap);
            tvCurrentPlayingArtist.setText(((MyGlobalVariables) this.getApplication()).CurrentArtist);
            tvCurrentPlayingAlbum.setText(((MyGlobalVariables) this.getApplication()).CurrentAlbum);
        }

        powerSwitch.setChecked(((MyGlobalVariables) this.getApplication()).IsPowerOn);

        //attach a listener to check for changes in state
        powerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sendPowerUpdate("on");
                } else {
                    sendPowerUpdate("off");
                }
            }
        });

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = intent.getStringExtra("type");

                if (s.equals("power")) {
                    String status = intent.getStringExtra("status");
                    if (status.equals("on")) {
                        powerSwitch.setChecked(true);
                        powerSwitch.setEnabled(true);
                    } else if (status.equals("off")) {
                        powerSwitch.setChecked(false);
                        powerSwitch.setEnabled(true);
                        currentLinearLayout.setVisibility(View.INVISIBLE);
                        setBusyInfo();
                    } else {
                        powerSwitch.setEnabled(false);
                        currentLinearLayout.setVisibility(View.INVISIBLE);
                        setBusyInfo();
                    }
                } else if (s.equals("busy")) {
                    textViewBusy.setText(((MyGlobalVariables) getApplication()).Status.getString());
                    switch (intent.getIntExtra("color", 0)) {
                        case 0:
                            findViewById(R.id.circle).setBackground(getResources().getDrawable(R.drawable.green_circle));
                            break;
                        case 1:
                            findViewById(R.id.circle).setBackground(getResources().getDrawable(R.drawable.red_circle));
                            break;
                        case 2:
                            findViewById(R.id.circle).setBackground(getResources().getDrawable(R.drawable.orange_circle));
                            break;
                    }
                } else if (s.equals("song")) {
                    CurrentSongTextView.setText(intent.getStringExtra("value"));
                }

            }
        };

        // use this to start and trigger a service
        startService(new Intent(this, ListenerService.class));
    }

    private void setBusyInfo() {
        findViewById(R.id.circle).setBackground(getResources().getDrawable(R.drawable.orange_circle));
        textViewBusy.setText(((MyGlobalVariables) this.getApplication()).Status.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_screen, menu);
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

    public void sendPowerUpdate(String status) {
        Intent intent = new Intent(this, SenderService.class);
        intent.putExtra("type", status);
        startService(intent);
    }

    public void requestScan(View view) {
        Intent intent = new Intent(this, SenderService.class);
        intent.putExtra("type", "scan");
        startService(intent);
    }

    public void goToCurrentList(View view) {
        Intent intent = new Intent(this, CurrentListScreen.class);
        intent.putExtra("type", "reload");
        startActivity(intent);
    }

    public void goToTotalList(View view) {
        Intent intent = new Intent(this, DatabaseService.class);
        intent.putExtra("type", "getAllArtists");
        startService(intent);
    }

    private void Test() {
        Intent intent = new Intent(this, ArtistAssociationScreen.class);
        intent.putExtra("newAlbumKey", "1,5,7");
        intent.putExtra("newAlbumBreaks", 5);
        startActivity(intent);
    }

    public void setIP(View view){
        Intent intent = new Intent(this,IPAssociationScreen.class);
        startActivity(intent);
    }

    public void getPower(View view){
        Intent intent = new Intent(this, SenderService.class);
        intent.putExtra("type", "getPower");
        startService(intent);
    }

    public void getStatus(View view){
        Intent intent = new Intent(this, SenderService.class);
        intent.putExtra("type", "getStatus");
        startService(intent);
    }

    private class DrawerItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    //Total List
                    goToTotalList(view);
                    break;
                case 1:
                    //Current List
                    if (((MyGlobalVariables) getApplication()).HasAlbum) {
                        goToCurrentList(view);
                    }
                    break;
                case 2:
                    //Settings
                    Test();
                    break;
            }
        }
    }
}
