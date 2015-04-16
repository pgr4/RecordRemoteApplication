package com.patrick.recordremoteapplication;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;


public class SettingsScreen extends ActionBarActivity {
    ArrayList<String> settingArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_settings_screen);
            readSettings();
            CheckBox checkBox = ((CheckBox) findViewById(R.id.checkbox));
            checkBox.setChecked(((MyGlobalVariables)this.getApplication()).IsAuto);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings_screen, menu);
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

    private void readSettings() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + "settings.txt"));

       settingArr = new ArrayList<String>();

        String line;
        while ((line = br.readLine()) != null) {
            if (!line.contains("Mode:")) {
                settingArr.add(line);
            }
        }

        br.close();
    }

    private void writeToFile(String val) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + "settings.txt", false));

            for (int i = 0; i < settingArr.size(); i++) {
                bw.write(settingArr.get(i));
                bw.newLine();
            }

            bw.write("Mode: " + val);

            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onAccept(View view) {
        try {
            CheckBox checkBox = ((CheckBox) findViewById(R.id.checkbox));
            if (checkBox.isChecked()) {
                ((MyGlobalVariables) getApplication()).IsAuto = true;
                writeToFile("auto");
            } else {
                ((MyGlobalVariables) getApplication()).IsAuto = false;
                writeToFile("manual");
            }
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
