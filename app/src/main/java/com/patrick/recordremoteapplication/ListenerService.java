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
            GetStatus();
            ListenAndWait(broadcastIP, port);

        } catch (Exception e) {
            Log.i("UDP", "no longer listening for UDP broadcasts cause of error " + e.getMessage());
        }
    }

    //Send Status
    private void SendStatus() {
        Intent intent = new Intent(this, SenderService.class);
        intent.putExtra("type", "status");
        startService(intent);
    }

    private void GetStatus() {
        Intent intent = new Intent(this, SenderService.class);
        intent.putExtra("type", "getStatus");
        startService(intent);
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
                        if (!mh.DestinationAddress.equals(((MyGlobalVariables) this.getApplication()).MyIp)) {
                            NewAlbum na = MessageParser.ParseNewAlbum(message, startingPoint);
                            showArtistAssociationScreen(na);
                        }
                        break;
                    case CurrentAlbum:
                        break;
                    case Status:
                        if (!mh.SourceAddress.equals(((MyGlobalVariables) this.getApplication()).MyIp)) {
                            SendStatus();
                        }
                        break;
                    case Sync:
                        if (!mh.SourceAddress.equals(((MyGlobalVariables) this.getApplication()).MyIp)) {
                            byte[] key = MessageParser.GetKey(message, startingPoint);

                            Intent intent = new Intent(this, DatabaseService.class);
                            intent.putExtra("type", "getAlbum");
                            intent.putExtra("key", key);
                            startService(intent);
                        }
                        break;
                    default:
                        ((MyGlobalVariables) this.getApplication()).Status = BusyStatus.fromInteger(mh.Command.getValue() - 20);
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
