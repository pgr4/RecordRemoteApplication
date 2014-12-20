package com.patrick.recordremoteapplication;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.channels.DatagramChannel;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ListenerService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.patrick.recordremoteapplication.action.FOO";
    private static final String ACTION_BAZ = "com.patrick.recordremoteapplication.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.patrick.recordremoteapplication.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.patrick.recordremoteapplication.extra.PARAM2";

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, ListenerService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public ListenerService() {
        super("ListenerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            //if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
//            } else if (ACTION_BAZ.equals(action)) {
//                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
//                handleActionBaz(param1, param2);
//            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        startListenForUDPBroadcast();
    }

    static String UDP_BROADCAST = "UDPBroadcast";

    //Boolean shouldListenForUDPBroadcast = false;
    DatagramSocket socket;

    private void listenAndWaitAndThrowIntent(InetAddress broadcastIP, Integer port) throws Exception {
        byte[] recvBuf = new byte[15000];

        DatagramSocket socket = new DatagramSocket(port);
        socket.setBroadcast(true);
        socket.setReuseAddress(true);

        if (socket != null && !socket.isClosed()) {
            while (true) {
                //socket.setSoTimeout(1000);
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(packet);

                String senderIP = packet.getAddress().getHostAddress();
                String message = new String(packet.getData()).trim();

                //Parse

                //broadcastIntent(senderIP, message);
            }
        }
    }

    private void broadcastIntent(String senderIP, String message) {
        Intent intent = new Intent(UDP_BROADCAST);
        intent.putExtra("sender", senderIP);
        intent.putExtra("message", message);
        sendBroadcast(intent);
    }

    void startListenForUDPBroadcast() {
        try {
            InetAddress broadcastIP = InetAddress.getByName("192.168.1.255");
            Integer port = 30003;
            listenAndWaitAndThrowIntent(broadcastIP, port);

        } catch (Exception e) {
            Log.i("UDP", "no longer listening for UDP broadcasts cause of error " + e.getMessage());
        }
    }


}
