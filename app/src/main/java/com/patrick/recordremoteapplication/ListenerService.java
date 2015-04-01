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
                        UpdateCurrentScreen(MessageParser.GetByte(message, startingPoint));
                        break;
                    case AtBeginning:
                        UpdateCurrentScreen();
                        break;
                    case SwitchPowerOn:
                    case SwitchPowerOff:
                    case GoToTrack:
                    case GoToBeginning:
                    case MediaPlay:
                    case MediaRewind:
                    case MediaSkip:
                    case MediaStop:
                        break;
                    default:
                        ((MyGlobalVariables) this.getApplication()).Status = BusyStatus.fromInteger(mh.Command.getValue() - 20);
                        switch (((MyGlobalVariables) this.getApplication()).Status) {
                            case Unknown:
                                UpdateMainScreen("busy", "Unknown");
                                break;
                            case Ready:
                                UpdateMainScreen("busy", "Ready");
                                break;
                            case Play:
                                UpdateMainScreen("busy", "Playing");
                                break;
                            case GoToTrack:
                                UpdateMainScreen("busy", "Going to Track");
                                break;
                            case Pause:
                                UpdateMainScreen("busy", "Pausing");
                                break;
                            case Stop:
                                UpdateMainScreen("busy", "Stopping");
                                break;
                            case Scan:
                                UpdateMainScreen("busy", "Scanning");
                                break;
                            default:
                                UpdateMainScreen("busy", "Unknown");
                                break;
                        }
                        break;
                }
            }
        }
    }

    //Update UI
    private void UpdateMainScreen(String type, String message) {
        Intent intent = new Intent("mainScreen");
        intent.putExtra("type", type);
        intent.putExtra("status", message);
        broadcaster.sendBroadcast(intent);
    }

    private void UpdateCurrentScreen() {
        Intent intent = new Intent("currentListScreen");
        intent.putExtra("type", "beginning");
        broadcaster.sendBroadcast(intent);
    }

    private void UpdateCurrentScreen(byte message) {
        Intent intent = new Intent("currentListScreen");
        intent.putExtra("type", "location");
        intent.putExtra("location", message);
        broadcaster.sendBroadcast(intent);
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
