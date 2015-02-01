package com.patrick.recordremoteapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;


public class IPAssociationScreen extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipassociation_screen);
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
            e.printStackTrace();
        }
    }

    //TODO:LOOP THROUGH FILE TO FIND EXISTING ENTRY AND REMOVE IT
    //TODO:SHOW TOOLTIP WHEN INCORRECT
    //TODO:SHOW NUMBER KEYBOARD
    private void writeIpToFile(String ipText) {
        try {
            final FileWriter fw = new FileWriter(getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + "settings.txt", false);

            new IpLookup() {

                @Override
                protected void onPostExecute(InetAddress result) {
                    try {
                        if (result != null) {
                            try {
                                fw.write("IPAddress: " + result);
                                fw.close();
                                goToMainScreen();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        fw.close();
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
}
