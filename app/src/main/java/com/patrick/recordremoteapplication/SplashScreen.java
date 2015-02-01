package com.patrick.recordremoteapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.InetAddress;

public class SplashScreen extends Activity {

    /**
     * Duration of wait *
     */
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    InetAddress ipAddress = null;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash_screen);
        /* New Handler to start the Menu-Activity 
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    final File file = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/settings.txt");
                    //IF the file exists get or add IP Address
                    //Else create file and add
                    if (file.exists()) {
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        String line;
                        String ipText = "";
                        while ((line = br.readLine()) != null) {
                            if (line.contains("IPAddress: ")) {
                                br.close();
                                ipText = line.replace("IPAddress: ", "").replace("/", "");
                                break;
                            }
                        }
                        br.close();
                        //IF there was a previous IP found make sure it is valid
                        //  IF invalid go associate
                        //  ELSE go to main screen
                        //ELSE go associate
                        if(ipText != "") {
                            new IpLookup() {

                                @Override
                                protected void onPostExecute(InetAddress result) {
                                    if (result == null) {
                                        goToIPAssociationScreen();
                                    } else {
                                        ((MyGlobalVariables) getApplication()).DatabaseIp = result;
                                        goToMainScreen();
                                    }
                                }
                            }.execute(ipText);
                        }
                        else {
                            goToIPAssociationScreen();
                        }
                    } else {
                        file.createNewFile();
                        goToIPAssociationScreen();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void goToMainScreen() {
        SplashScreen.this.finish();
        Intent intent = new Intent(this, MainScreen.class);
        startActivity(intent);
    }

    private void goToIPAssociationScreen() {
        SplashScreen.this.finish();
        Intent intent = new Intent(this, IPAssociationScreen.class);
        startActivity(intent);
    }
}