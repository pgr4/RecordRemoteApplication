package com.patrick.recordremoteapplication;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;

import org.apache.http.conn.util.InetAddressUtils;

public class Utils {

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                                return delim < 0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
    }

    public static String IntArrayToString(int[] key) {
        String ret = "";
        for (int i = 0; i < key.length; i++) {

            ret += key[i];

            if (i != key.length - 1) {
                ret += ",";
            }
        }

        return ret;
    }

    public static int[] StringToIntArray(String key) {
        String[] total = key.split(",");
        int[] ret = new int[total.length];
        for (int i = 0; i < total.length; i++) {
            ret[i] = Integer.parseInt(total[i]);
        }
        return ret;
    }

    public static int byteArrayToInt(byte[] b) {
        ByteBuffer wrapped = ByteBuffer.wrap(b); // big-endian by default
        return wrapped.getShort();
    }

    public static byte[] intToByteArray(int i) {
        ByteBuffer dbuf = ByteBuffer.allocate(2);
        dbuf.putShort((short) i);
        return dbuf.array();
    }
}