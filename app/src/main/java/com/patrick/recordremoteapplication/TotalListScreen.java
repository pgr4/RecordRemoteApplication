package com.patrick.recordremoteapplication;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

//TODO:Whenever we come back to this screen we need to check for updates.
//TODO: Need to clear the Global Variable Artists on close
public class TotalListScreen extends ActionBarActivity {

    private ListView mainListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_list_screen);

        //Bundle b = getIntent().getExtras();
        //b.getParcelableArrayList("list");

        ArrayList<JsonArtist> list = ((MyGlobalVariables) this.getApplication()).Artists;

        //Get the layout
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        LinearLayout linearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(lp);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        for(int i = 0;i<list.size();i++){
            //Add the Artist Name
            TextView artistText = new TextView(this);
            artistText.setText(list.get(i).Name);
            linearLayout.addView(artistText);

            for(int j=0; j<list.get(i).Albums.size();j++) {
                //Add the Album Name
                TextView albumText = new TextView(this);
                albumText.setText(list.get(i).Albums.get(j).Name);
                linearLayout.addView(albumText);

                //Add the Songs
                for(int k=0;k<list.get(i).Albums.get(j).Songs.size();k++){
                    TextView songText = new TextView(this);
                    songText.setText(list.get(i).Albums.get(j).Songs.get(k));
                    linearLayout.addView(songText);
                }
            }
        }
        scrollView.addView(linearLayout);

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
