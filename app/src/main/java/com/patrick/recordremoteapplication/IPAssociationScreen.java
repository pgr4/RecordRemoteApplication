package com.patrick.recordremoteapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;


public class IPAssociationScreen extends ActionBarActivity {

    ArrayList<String> settingArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_ipassociation_screen);
            settingArr = readSettings();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ipassociation_screen, menu);
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

    public void goToMainScreen(View view) {
        try {
            String editText = ((EditText) findViewById(R.id.editText)).getText().toString();
            InetAddress inetAddress = InetAddress.getByName(editText);
            writeIpToFile(inetAddress.toString());

        } catch (Exception e) {
            showError();
            e.printStackTrace();
        }
    }

    private ArrayList<String> readSettings() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + "settings.txt"));

        final ArrayList<String> settingArr = new ArrayList<String>();

        String line;
        while ((line = br.readLine()) != null) {
            if (line.contains("IPAddress:") == false) {
                settingArr.add(line);
            }
        }

        br.close();

        return settingArr;
    }

    //TODO:SHOW NUMBER KEYBOARD
    private void writeIpToFile(String ipText) {
        try {
            new IpLookup() {

                @Override
                protected void onPostExecute(InetAddress result) {
                    try {

                        if (result != null) {

                            BufferedWriter bw = new BufferedWriter(new FileWriter(getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + "settings.txt", false));

                            for (int i = 0; i < settingArr.size(); i++) {
                                bw.write(settingArr.get(i));
                            }

                            bw.write("IPAddress: " + result);
                            bw.close();
                            goToMainScreen();

                        } else {
                            showError();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.execute(ipText);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToMainScreen() {
        Intent intent = new Intent(this, MainScreen.class);
        startActivity(intent);
    }

    public void showError() {
        TextView textView = ((TextView) findViewById(R.id.errorText));
        textView.setVisibility(View.VISIBLE);
    }

}
