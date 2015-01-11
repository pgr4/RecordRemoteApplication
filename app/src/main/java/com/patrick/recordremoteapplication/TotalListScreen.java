package com.patrick.recordremoteapplication;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

//todo:Whenever we come back to this screen we need to check for updates.
public class TotalListScreen extends ActionBarActivity {

    private ListView mainListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_list_screen);

        Bundle b = getIntent().getExtras();

        ArrayList<TotalListSong> list = b.getParcelableArrayList("list");

        //Get the List
        mainListView = (ListView) findViewById(R.id.currentListView);

        //Setup the adapter
        TotalListAdapter adapter = new TotalListAdapter(this, list);
        mainListView.setAdapter(adapter);

        View v = getWindow().getDecorView().findViewById(android.R.id.content);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_total_list_screen, menu);
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
