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

        if (!socket.isClosed()) {
            while (true) {
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(packet);

                String senderIP = packet.getAddress().getHostAddress();
                byte[] message = packet.getData();

                //Parse the header
                MessageHeader mh = MessageParser.ParseMessageHeader(message);

                //Starting iterator point after the header has been parsed.
                int startingPoint = 15;

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
                            intent.putExtra("type", "syncAlbum");
                            intent.putExtra("key", key);
                            startService(intent);
                        }
                        break;
                    case PowerUnknown:
                        if (!mh.SourceAddress.equals(((MyGlobalVariables) this.getApplication()).MyIp)) {
                            ((MyGlobalVariables) this.getApplication()).IsPowerOn = false;

                            PowerUpdate("unknown");
                        }
                        break;
                    case On:
                        if (!mh.SourceAddress.equals(((MyGlobalVariables) this.getApplication()).MyIp)) {
                            ((MyGlobalVariables) this.getApplication()).IsPowerOn = true;

                            PowerUpdate("on");
                        }
                        break;
                    case Off:
                        if (!mh.SourceAddress.equals(((MyGlobalVariables) this.getApplication()).MyIp)) {
                            ((MyGlobalVariables) this.getApplication()).IsPowerOn = false;
                            ((MyGlobalVariables) this.getApplication()).Status = BusyStatus.Unknown;
                            ((MyGlobalVariables) this.getApplication()).CurrentSong = null;
                            ((MyGlobalVariables) this.getApplication()).CurrentAlbum = null;
                            ((MyGlobalVariables) this.getApplication()).CurrentBitmap = null;
                            ((MyGlobalVariables) this.getApplication()).CurrentArtist = null;
                            ((MyGlobalVariables) this.getApplication()).CurrentKey = null;
                            ((MyGlobalVariables) this.getApplication()).HasAlbum = false;
                            ((MyGlobalVariables) this.getApplication()).CurrentSongList = null;

                            PowerUpdate("off");
                        }
                        break;
                    case GetPower:
                        if (!mh.SourceAddress.equals(((MyGlobalVariables) this.getApplication()).MyIp)) {
                            Intent intent = new Intent(this, SenderService.class);
                            intent.putExtra("type", "sendPower");
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
                    case sReady:
                        ((MyGlobalVariables) this.getApplication()).Status = BusyStatus.Ready;
                        BusyUpdate("Ready", 0);
                        break;
                    case sGoToTrack:
                        ((MyGlobalVariables) this.getApplication()).IsPlaying = false;
                        PlayingUpdate(false);
                        ((MyGlobalVariables) this.getApplication()).Status = BusyStatus.GoToTrack;
                        BusyUpdate("Going to Track", 1);
                        break;
                    case sPause:
                        ((MyGlobalVariables) this.getApplication()).IsPlaying = false;
                        PlayingUpdate(false);
                        ((MyGlobalVariables) this.getApplication()).Status = BusyStatus.Pause;
                        BusyUpdate("Pausing", 1);
                        break;
                    case sPlay:
                        ((MyGlobalVariables) this.getApplication()).IsPlaying = true;
                        PlayingUpdate(true);
                        ((MyGlobalVariables) this.getApplication()).Status = BusyStatus.Play;
                        BusyUpdate("Playing", 1);
                        break;
                    case sScan:
                        ((MyGlobalVariables) this.getApplication()).Status = BusyStatus.Scan;
                        BusyUpdate("Scanning", 1);
                        break;
                    case sStop:
                        ((MyGlobalVariables) this.getApplication()).Status = BusyStatus.Stop;
                        BusyUpdate("Stopping", 1);
                        break;
                    case sUnknown:
                        ((MyGlobalVariables) this.getApplication()).Status = BusyStatus.Unknown;
                        BusyUpdate("Unknown", 2);
                        break;
                    default:
                        throw new Exception();
                }
            }
        }
    }

    private void PositionUpdate(byte message) {
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

    private void PositionUpdate() {
        Intent intent = new Intent("mainScreen");
        intent.putExtra("type", "song");
        intent.putExtra("value", ((MyGlobalVariables) this.getApplication()).CurrentSongList.get(0));
        ((MyGlobalVariables) this.getApplication()).CurrentSong = ((MyGlobalVariables) this.getApplication()).CurrentSongList.get(0);
        broadcaster.sendBroadcast(intent);

        Intent intent1 = new Intent("currentListScreen");
        intent1.putExtra("type", "beginning");
        broadcaster.sendBroadcast(intent1);
    }

    private void BusyUpdate(String message, int type) {
        Intent intent = new Intent("mainScreen");
        intent.putExtra("type", "busy");
        //Not needed
        intent.putExtra("status", message);
        intent.putExtra("color", type);
        broadcaster.sendBroadcast(intent);
    }

    private void PowerUpdate(String message) {
        Intent intent = new Intent("mainScreen");
        intent.putExtra("type", "power");
        intent.putExtra("status", message);
        broadcaster.sendBroadcast(intent);

        Intent intent1 = new Intent("currentListScreen");
        intent1.putExtra("type", "power");
        intent1.putExtra("status", message);
        broadcaster.sendBroadcast(intent1);
    }

    private void PlayingUpdate(boolean message) {
        Intent intent = new Intent("currentListScreen");
        intent.putExtra("type", "isPlaying");
        intent.putExtra("value", message);
        broadcaster.sendBroadcast(intent);
    }
}
