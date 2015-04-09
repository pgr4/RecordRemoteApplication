package com.patrick.recordremoteapplication;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class TotalAlbumScreen extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_album_screen);

        ListView lv = (ListView) findViewById(R.id.listView);

        TotalAlbumListAdapter adapter = new TotalAlbumListAdapter(this, ((MyGlobalVariables) this.getApplication()).Albums);

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                startDatabaseService(((JsonAlbum) adapter.getAdapter().getItem(position)).Key);
            }
        });
    }

    //Start the Database service to get a list of Albums associated with the selected item
    private void startDatabaseService(String s) {
        Intent intent = new Intent(this, DatabaseService.class);
        intent.putExtra("type", "getSongs");
        intent.putExtra("key", s);
        startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_total_album_screen, menu);
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
