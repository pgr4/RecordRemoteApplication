package com.patrick.recordremoteapplication;

import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by pat on 12/28/2014.
 */
public class MessageParser {
    public static NewAlbum ParseNewAlbum(byte[] message, int startingPoint) {
        byte[] arr = new byte[2];

        for (int i = 0; i < 2; i++) {
            arr[i] = message[startingPoint + i];
        }
        int breaks = message[startingPoint + 2];

        return new NewAlbum(breaks, arr);
    }

    public static MessageHeader ParseMessageHeader(byte[] message) throws UnknownHostException {
        InetAddress sourceIP = GetIP(message, 0);
        InetAddress destinationIP = GetIP(message, 4);
        MessageCommand command = GetCommand(message, 8);
        boolean valid = IsEndOfHeader(message, 9);
        if (!valid) {
            return null;
        } else {
            return new MessageHeader(sourceIP, destinationIP, command);
        }
    }

    public static byte[] GetKey(byte[] message, int startPoint){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(message,startPoint,message.length - startPoint);
        return byteArrayOutputStream.toByteArray();
    }

    public static InetAddress GetIP(byte[] message, int startPoint) throws UnknownHostException {
        byte[] arr = new byte[4];

        for (int i = 0; i < 4; i++) {
            arr[i] = message[startPoint + i];
        }

        return InetAddress.getByAddress(arr);
    }

    public static MessageCommand GetCommand(byte[] message, int startPoint) {
        return fromValue(message[startPoint]);
    }

    public static boolean IsEndOfHeader(byte[] message, int startPoint) {
        for (int i = 0; i < 6; i++) {
            if (message[startPoint + i] != 111) {
                return false;
            }
        }

        return true;
    }

    public static MessageCommand fromValue(byte value) {
        switch (value) {
            case 0:
                return MessageCommand.None;
            case 1:
                return MessageCommand.NewAlbum;
            case 2:
                return MessageCommand.CurrentAlbum;
            case 3:
                return MessageCommand.Status;
            case 20:
                return MessageCommand.Busy;
            case 21:
                return MessageCommand.Ready;
            default:
                return MessageCommand.None;
        }
    }
}
