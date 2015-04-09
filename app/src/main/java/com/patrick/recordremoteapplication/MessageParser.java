package com.patrick.recordremoteapplication;

import org.apache.http.util.ByteArrayBuffer;

import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by pat on 12/28/2014.
 */
public class MessageParser {
    public static NewAlbum ParseNewAlbum(byte[] message, int startingPoint) {
        int endingPoint = 0;
        for (int i = startingPoint; i < message.length; i++) {
            if (message[i] == 111 && message[i + 1] == 111 && message[i + 2] == 111 &&
                    message[i + 3] == 111 && message[i + 4] == 111 && message[i + 5] == 111) {
                endingPoint = i - startingPoint;
            }
        }
        byte[] arr = new byte[endingPoint];

        for (int i = 0; i < endingPoint; i++) {
            arr[i] = message[startingPoint + i];
        }

        int breaks = endingPoint;

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

    public static byte[] GetKey(byte[] message, int startPoint) {
        ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer(100);
        for (int i = startPoint; i < message.length; i++) {
            if (message[i] == 111 && message[i + 1] == 111 && message[i + 2] == 111 &&
                    message[i + 3] == 111 && message[i + 4] == 111 && message[i + 5] == 111) {
                break;
            } else {
                byteArrayBuffer.append(message[i]);
            }
        }
        return byteArrayBuffer.toByteArray();
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

    public static byte GetByte(byte[] message, int startPoint) {
        return message[startPoint];
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
            case 4:
                return MessageCommand.Scan;
            case 5:
                return MessageCommand.Sync;
            case 6:
                return MessageCommand.GetPower;
            case 7:
                return MessageCommand.SwitchPowerOn;
            case 8:
                return MessageCommand.SwitchPowerOff;
            case 9:
                return MessageCommand.UpdatePosition;
            case 10:
                return MessageCommand.AtBeginning;
            case 14:
                return MessageCommand.PowerUnknown;
            case 15:
                return MessageCommand.On;
            case 16:
                return MessageCommand.Off;
            case 20:
                return MessageCommand.sUnknown;
            case 21:
                return MessageCommand.sReady;
            case 22:
                return MessageCommand.sPlay;
            case 23:
                return MessageCommand.sGoToTrack;
            case 24:
                return MessageCommand.sPause;
            case 25:
                return MessageCommand.sStop;
            case 26:
                return MessageCommand.sScan;
            case 30:
                return MessageCommand.GoToTrack;
            case 31:
                return MessageCommand.MediaPlay;
            case 32:
                return MessageCommand.MediaStop;
            case 33:
                return MessageCommand.GoToBeginning;
            case 34:
                return MessageCommand.QueueGoToTrack;
            case 35:
                return MessageCommand.QueueGoToBeginning;
            default:
                return MessageCommand.None;
        }
    }
}
