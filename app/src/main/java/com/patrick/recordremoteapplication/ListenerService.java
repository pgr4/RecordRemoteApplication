package com.patrick.recordremoteapplication;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ListenerService extends IntentService {
    private LocalBroadcastManager broadcaster;

    public ListenerService() {
        super("ListenerService");
        broadcaster = LocalBroadcastManager.getInstance(this);
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
        }
    }

    //Send our Status
    private void SendStatus() {
        Intent intent = new Intent(this, SenderService.class);
        intent.putExtra("type", "status");
        startService(intent);
    }

    //Request Status
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
                        if (mh.DestinationAddress.equals(((MyGlobalVariables) this.getApplication()).MyIp)) {
                            NewAlbum na = MessageParser.ParseNewAlbum(message, startingPoint);

                            Intent intent = new Intent(this, DatabaseService.class);
                            intent.putExtra("type", "isAlbumNew");
                            intent.putExtra("breaks", na.Breaks);
                            intent.putExtra("key", na.Key);
                            startService(intent);
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
                    case PowerUnknown:
                        if (!mh.SourceAddress.equals(((MyGlobalVariables) this.getApplication()).MyIp)) {
                            ((MyGlobalVariables) this.getApplication()).IsPowerOn = false;
                            UpdateMainScreen("power", "unknown");
                        }
                        break;
                    case On:
                        if (!mh.SourceAddress.equals(((MyGlobalVariables) this.getApplication()).MyIp)) {
                            ((MyGlobalVariables) this.getApplication()).IsPowerOn = true;
                            UpdateMainScreen("power", "on");
                        }
                        break;
                    case Off:
                        if (!mh.SourceAddress.equals(((MyGlobalVariables) this.getApplication()).MyIp)) {
                            ((MyGlobalVariables) this.getApplication()).IsPowerOn = false;
                            ((MyGlobalVariables) this.getApplication()).CurrentSong = null;
                            ((MyGlobalVariables) this.getApplication()).CurrentAlbum = null;
                            ((MyGlobalVariables) this.getApplication()).CurrentBitmap = null;
                            ((MyGlobalVariables) this.getApplication()).CurrentArtist = null;
                            ((MyGlobalVariables) this.getApplication()).CurrentKey = null;
                            ((MyGlobalVariables) this.getApplication()).HasAlbum = false;
                            ((MyGlobalVariables) this.getApplication()).CurrentSongList.clear();

                            UpdateMainScreen("power", "off");
                        }
                        break;
                    case GetPower:
                        if (!mh.SourceAddress.equals(((MyGlobalVariables) this.getApplication()).MyIp)) {
                            Intent intent = new Intent(this, SenderService.class);
                            intent.putExtra("type", "sendPower");
                            intent.putExtra("power", ((MyGlobalVariables) this.getApplication()).IsPowerOn);
                            startService(intent);
                        }
                        break;
                    case UpdatePosition:
                        byte b = MessageParser.GetByte(message, startingPoint);
                        PositionUpdate(b);
                        break;
                    case AtBeginning:
                        PositionUpdate();
                        break;
                    case Scan:
                    case SwitchPowerOn:
                    case SwitchPowerOff:
                    case GoToTrack:
                    case GoToBeginning:
                    case MediaPlay:
                    case MediaStop:
                        break;
                    case sGoToTrack:
                        UpdateMainScreen("busy", "Going to Track");
                        ((MyGlobalVariables) this.getApplication()).IsPlaying = false;
                        UpdateCurrentScreen(false);
                        break;
                    case sPause:
                        UpdateMainScreen("busy", "Pausing");
                        ((MyGlobalVariables) this.getApplication()).IsPlaying = false;
                        UpdateCurrentScreen(false);
                        break;
                    case sPlay:
                        UpdateMainScreen("busy", "Playing");
                        ((MyGlobalVariables) this.getApplication()).IsPlaying = true;
                        UpdateCurrentScreen(true);
                        break;
                    case sReady:
                        UpdateMainScreen("busy", "Ready");
                        break;
                    case sScan:
                        UpdateMainScreen("busy", "Scanning");
                        break;
                    case sStop:
                        UpdateMainScreen("busy", "Stopping");
                        break;
                    case sUnknown:
                        UpdateMainScreen("busy", "Unknown");
                        break;
                    default:
                        throw new Exception();
                }
            }
        }
    }

    private void PositionUpdate(byte message){
        Intent intent = new Intent("mainScreen");
        intent.putExtra("type", "song");
        for (int i = 0; i < ((MyGlobalVariables) this.getApplication()).CurrentKey.length; i++) {
            if (((MyGlobalVariables) this.getApplication()).CurrentKey[i] == message) {

                intent.putExtra("value", ((MyGlobalVariables) this.getApplication()).CurrentSongList.get(i));
                ((MyGlobalVariables) this.getApplication()).CurrentSong = ((MyGlobalVariables) this.getApplication()).CurrentSongList.get(i);
            }
        }
        broadcaster.sendBroadcast(intent);

        Intent intent1 = new Intent("currentListScreen");
        intent1.putExtra("type", "location");
        intent1.putExtra("location", message);
        broadcaster.sendBroadcast(intent1);
    }

    private void PositionUpdate(){
        Intent intent = new Intent("mainScreen");
        intent.putExtra("type", "song");
        intent.putExtra("value", ((MyGlobalVariables) this.getApplication()).CurrentSongList.get(0));
        ((MyGlobalVariables) this.getApplication()).CurrentSong = ((MyGlobalVariables) this.getApplication()).CurrentSongList.get(0);
        broadcaster.sendBroadcast(intent);

        Intent intent1 = new Intent("currentListScreen");
        intent1.putExtra("type", "beginning");
        broadcaster.sendBroadcast(intent1);
    }

    private void UpdateMainScreen(String type, String message) {
        Intent intent = new Intent("mainScreen");
        intent.putExtra("type", type);
        intent.putExtra("status", message);
        broadcaster.sendBroadcast(intent);
    }

    private void UpdateCurrentScreen(boolean message) {
        Intent intent = new Intent("currentListScreen");
        intent.putExtra("type", "isPlaying");
        intent.putExtra("value", message);
        broadcaster.sendBroadcast(intent);
    }
}
