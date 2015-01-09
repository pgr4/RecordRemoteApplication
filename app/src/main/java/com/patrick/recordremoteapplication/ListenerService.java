package com.patrick.recordremoteapplication;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

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
            ListenAndWait(broadcastIP, port);

        } catch (Exception e) {
            Log.i("UDP", "no longer listening for UDP broadcasts cause of error " + e.getMessage());
        }
    }

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
