package com.patrick.recordremoteapplication;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ListenerService extends IntentService {
    public ListenerService() {
        super("ListenerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            InitializeUdpBroadcast();
        }
    }

    void InitializeUdpBroadcast() {
        try {
            InetAddress broadcastIP = InetAddress.getByName("192.168.1.255");
            Integer port = 30003;
            GetStatus(broadcastIP, port);
            ListenAndWait(broadcastIP, port);

        } catch (Exception e) {
            Log.i("UDP", "no longer listening for UDP broadcasts cause of error " + e.getMessage());
        }
    }

    //Send Status
    private void SendStatus(InetAddress ip, Integer port) throws IOException {
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

        if(((MyGlobalVariables) this.getApplication()).IsSystemBusy) {
            buf[pointer++] = 20;
        }else{
            buf[pointer++] = 21;
        }

        for (int i = pointer; i < pointer + 6; i++) {
            buf[i] = 111;
        }

        if(((MyGlobalVariables) this.getApplication()).IsSystemBusy) {
            buf[pointer++] = statusTypeToByte(((MyGlobalVariables) this.getApplication()).StatusExtra);
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, port);
        socket.send(packet);
        socket.close();
    }

    private StatusType byteToStatusType(byte b){
        switch (b){
            case 0:
                return StatusType.Play;
            case 1:
                return StatusType.GoToTrack;
            case 2:
                return StatusType.Pause;
            case 3:
                return StatusType.Stop;
            default:
                return StatusType.None;
        }
    }

    private byte statusTypeToByte(StatusType st){
        switch (st){
            case Play:
                return 0x00;
            case GoToTrack:
                return 0x01;
            case Pause:
                return 0x02;
            case Stop:
                return 0x03;
            default:
                return 0x00;
        }
    }

    //Request Status
    private void GetStatus(InetAddress ip, Integer port) throws IOException {
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

        buf[pointer++] = 3;

        for (int i = pointer; i < pointer + 6; i++) {
            buf[i] = 111;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, port);
        socket.send(packet);
        socket.close();
    }

    //Main Listening function
    //Sits here listening for incoming messages
    private void ListenAndWait(InetAddress broadcastIP, Integer port) throws Exception {
        byte[] recvBuf = new byte[15000];

        DatagramSocket socket = new DatagramSocket(port);
        socket.setBroadcast(true);
        socket.setReuseAddress(true);

        if (socket != null && !socket.isClosed()) {
            while (true) {
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(packet);

                String senderIP = packet.getAddress().getHostAddress();
                byte[] message = packet.getData();

                //Parse the header
                MessageHeader mh = MessageParser.ParseMessageHeader(message);

                //Starting iterator point after the header has been parsed.
                int startingPoint = 15;

                //Before we create the intent to show the CurrentListScreen we need to check if the
                //album is not in our database. If it is not then we need to show the
                //AlbumAssociationView.
                switch (mh.Command) {
                    case None:
                        break;
                    case NewAlbum:
                        NewAlbum na = MessageParser.ParseNewAlbum(message, startingPoint);
                        showCurrentListScreen(na);
                        break;
                    case CurrentAlbum:
                        break;
                    case Status:
                        SendStatus(broadcastIP, port);//TODO:SEND UDP MESSAGE WHETHER WE ARE READY OR NOT
                        break;
                    case Busy:
                        ((MyGlobalVariables)this.getApplication()).IsSystemBusy = true;
                        ((MyGlobalVariables)this.getApplication()).StatusExtra = byteToStatusType(message[startingPoint]);
                        break;
                    case Ready:
                        ((MyGlobalVariables)this.getApplication()).IsSystemBusy = false;
                        ((MyGlobalVariables)this.getApplication()).StatusExtra = StatusType.None;
                        break;

                }
            }
        }
    }

    //Bring up the currentListScreen with the NewAlbum information
    private void showCurrentListScreen(NewAlbum na) {
        Intent intent = new Intent(this, CurrentListScreen.class);
        intent.putExtra("newAlbumBreaks", na.Breaks);
        intent.putExtra("newAlbumKey", na.Key);
        //This is necessary
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //Bring up the currentListScreen with the NewAlbum information
    private void showArtistAssociationScreen(NewAlbum na) {
        Intent intent = new Intent(this, ArtistAssociationScreen.class);
        intent.putExtra("newAlbumBreaks", na.Breaks);
        intent.putExtra("newAlbumKey", na.Key);
        //This is necessary
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
