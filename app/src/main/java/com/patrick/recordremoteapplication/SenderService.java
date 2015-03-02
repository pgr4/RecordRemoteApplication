package com.patrick.recordremoteapplication;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

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
                        sendScan();
                        break;
                    case "getStatus":
                        getStatus();
                        break;
                    case "status":
                        sendStatus();
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendSync(byte[] key) throws IOException {
        final DatagramSocket socket = new DatagramSocket();
        int pointer = 0;
        byte[] buf = new byte[256];

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
            buf[pointer++] = key[i];
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, port);
        socket.send(packet);
        socket.close();
    }

    //Send Status
    private void sendStatus() throws IOException {
        final DatagramSocket socket = new DatagramSocket();
        int pointer = 0;
        byte[] buf = new byte[256];

        for (int i = 0; i < 4; i++) {
            buf[i] = ((MyGlobalVariables) this.getApplication()).MyIp.getAddress()[i];
        }

        pointer = 4;

        for (int i = pointer; i < pointer + 4; i++) {
            buf[i] = 12;
        }
        pointer = 8;

        if (((MyGlobalVariables) this.getApplication()).IsSystemBusy) {
            buf[pointer++] = 20;
        } else {
            if (((MyGlobalVariables) this.getApplication()).BusyStatusExtra == BusyStatus.Unknown) {
                buf[pointer++] = 20;
            } else {
                buf[pointer++] = 21;
            }
        }

        for (int i = pointer; i < pointer + 6; i++) {
            buf[i] = 111;
        }

        pointer = 15;

        if (((MyGlobalVariables) this.getApplication()).IsSystemBusy ||
                ((MyGlobalVariables) this.getApplication()).BusyStatusExtra == BusyStatus.Unknown) {
            buf[pointer++] = ((MyGlobalVariables) this.getApplication()).BusyStatusExtra.getValue();
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, port);
        socket.send(packet);
        socket.close();
    }

    //Request Status
    private void getStatus() throws IOException {
        final DatagramSocket socket = new DatagramSocket();
        int pointer = 0;
        byte[] buf = new byte[256];

        for (int i = 0; i < 4; i++) {
            buf[i] = ((MyGlobalVariables) this.getApplication()).MyIp.getAddress()[i];
        }

        pointer = 4;

        for (int i = pointer; i < pointer + 4; i++) {
            buf[i] = 12;
        }
        pointer = 8;

        buf[pointer++] = (byte) MessageCommand.Status.getValue();

        for (int i = pointer; i < pointer + 6; i++) {
            buf[i] = 111;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, port);
        socket.send(packet);
        socket.close();
    }

    void sendScan() throws IOException {
        final DatagramSocket socket = new DatagramSocket();
        int pointer = 0;
        byte[] buf = new byte[256];

        for (int i = 0; i < 4; i++) {
            buf[i] = ((MyGlobalVariables) this.getApplication()).MyIp.getAddress()[i];
        }

        pointer = 4;

        for (int i = pointer; i < pointer + 4; i++) {
            buf[i] = 12;
        }
        pointer = 8;

        buf[pointer++] = 4;

        for (int i = pointer; i < pointer + 6; i++) {
            buf[i] = 111;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, port);
        socket.send(packet);
        socket.close();
    }
}
