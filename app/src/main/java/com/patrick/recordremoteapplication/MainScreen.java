package com.patrick.recordremoteapplication;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class MainScreen extends ActionBarActivity {
    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

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

        ((MyGlobalVariables) this.getApplication()).Status = BusyStatus.Unknown;

        mNavigationDrawerItemTitles = getResources().getStringArray(R.array.drawerActivities);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.drawer_item, mNavigationDrawerItemTitles);
        mDrawerList.setAdapter(adapter);

        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // use this to start and trigger a service
        startService(new Intent(this, ListenerService.class));
    }

    public void requestScan(View view) {
        Intent intent = new Intent(this, SenderService.class);
        intent.putExtra("type", "scan");
        startService(intent);
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

    public void goToCurrentList(View view) {
//        Intent intent = new Intent(this, CurrentListScreen.class);
//        intent.putExtra("newAlbumBreaks", 5);
//        intent.putExtra("newAlbumKey", new byte[]{24, 43});
//        startActivity(intent);
    }

    public void goToTotalList(View view) {
        Intent intent = new Intent(this, DatabaseService.class);
        intent.putExtra("type", "getAllArtists");
        startService(intent);
    }

    public void goToArtistAssociation(View view) {
        Intent intent = new Intent(this, ArtistAssociationScreen.class);
        intent.putExtra("newAlbumBreaks", 5);
        intent.putExtra("newAlbumKey", new byte[]{24, 43});
        startActivity(intent);
    }

    public void launchWebService(View view) {
        Intent intent = new Intent(this, DatabaseService.class);
        intent.putExtra("type", "getAllArtists");
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
                    goToCurrentList(view);
                    break;
                case 2:
                    //Settings
                    break;
            }
        }
    }
}
