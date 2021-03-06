package com.patrick.recordremoteapplication;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by pat on 3/1/2015.
 */
public class SenderService extends IntentService {

    public SenderService() {
        super("SenderService");
    }

    InetAddress ip;
    Integer port = 30003;

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            ip = InetAddress.getByName("192.168.1.255");

            if (intent != null) {
                Bundle b = intent.getExtras();

                switch (b.getString("type")) {
                    case "scan":
                        sendHeader(MessageCommand.Scan.getValue());
                        break;
                    case "sync":
                        sendSync(b.getIntArray("key"));
                        break;
                    case "requestSync":
                        sendHeader(MessageCommand.RequestSync.getValue());
                        break;
                    case "getStatus":
                        sendHeader(MessageCommand.Status.getValue());
                        break;
                    case "status":
                        sendStatus();
                        break;
                    case "on":
                        sendHeader(MessageCommand.SwitchPowerOn.getValue());
                        break;
                    case "off":
                        sendHeader(MessageCommand.SwitchPowerOff.getValue());
                        break;
                    case "getPower":
                        sendHeader(MessageCommand.GetPower.getValue());
                        break;
                    case "sendPower":
                        if (((MyGlobalVariables) getApplication()).IsPowerOn) {
                            sendHeader(MessageCommand.On.getValue());
                        } else {
                            sendHeader(MessageCommand.Off.getValue());
                        }
                        break;
                    case "playBeginning":
                        sendHeader(MessageCommand.GoToBeginning.getValue());
                        break;
                    case "playTrack":
                        sendGoToTrack(b.getInt("location"));
                        break;
                    case "play":
                        sendHeader(MessageCommand.MediaPlay.getValue());
                        break;
                    case "pause":
                        sendHeader(MessageCommand.MediaStop.getValue());
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendHeader(int t) throws IOException {
        final DatagramSocket socket = new DatagramSocket();
        int pointer = 0;
        byte[] buf = new byte[15];

        for (int i = 0; i < 4; i++) {
            buf[i] = ((MyGlobalVariables) this.getApplication()).MyIp.getAddress()[i];
        }

        pointer = 4;

        for (int i = pointer; i < pointer + 4; i++) {
            buf[i] = 12;
        }
        pointer = 8;

        buf[pointer++] = (byte) t;

        for (int i = pointer; i < pointer + 6; i++) {
            buf[i] = 111;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, port);
        socket.send(packet);
        socket.close();
    }

    //Send Sync
    private void sendSync(int[] key) throws IOException {
        final DatagramSocket socket = new DatagramSocket();
        int pointer = 0;
        byte[] buf = new byte[15 + (key.length * 2) + 6];

        for (int i = 0; i < 4; i++) {
            buf[i] = ((MyGlobalVariables) this.getApplication()).MyIp.getAddress()[i];
        }

        pointer = 4;

        for (int i = pointer; i < pointer + 4; i++) {
            buf[i] = 12;
        }

        pointer = 8;

        buf[pointer++] = 5;

        for (int i = pointer; i < pointer + 6; i++) {
            buf[i] = 111;
        }

        pointer = 15;

        for (int i = 0; i < key.length; i++) {
            byte[] b = Utils.intToByteArray(key[i]);
            buf[pointer++] = b[0];
            buf[pointer++] = b[1];
        }

        for(int i = 0; i<6; i++){
            buf[pointer++] = 111;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, port);
        socket.send(packet);
        socket.close();
    }

    //Send Status
    private void sendStatus() throws IOException {
        final DatagramSocket socket = new DatagramSocket();
        int pointer = 0;
        byte[] buf = new byte[15];

        for (int i = 0; i < 4; i++) {
            buf[i] = ((MyGlobalVariables) this.getApplication()).MyIp.getAddress()[i];
        }

        pointer = 4;

        for (int i = pointer; i < pointer + 4; i++) {
            buf[i] = 12;
        }
        pointer = 8;

        buf[pointer++] = (byte) (((MyGlobalVariables) this.getApplication()).Status.getValue() + 20);

        for (int i = pointer; i < pointer + 6; i++) {
            buf[i] = 111;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, port);
        socket.send(packet);
        socket.close();
    }

    private void sendGoToTrack(int loc) throws IOException {
        final DatagramSocket socket = new DatagramSocket();
        int pointer = 0;
        byte[] buf = new byte[17];

        for (int i = 0; i < 4; i++) {
            buf[i] = ((MyGlobalVariables) this.getApplication()).MyIp.getAddress()[i];
        }

        pointer = 4;

        for (int i = pointer; i < pointer + 4; i++) {
            buf[i] = 12;
        }
        pointer = 8;

        buf[pointer++] = (byte) MessageCommand.GoToTrack.getValue();

        for (int i = pointer; i < pointer + 6; i++) {
            buf[i] = 111;
        }

        byte[] bLoc = Utils.intToByteArray(loc);

        buf[15] = bLoc[0];
        buf[16] = bLoc[1];

        DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, port);
        socket.send(packet);
        socket.close();
    }
}
