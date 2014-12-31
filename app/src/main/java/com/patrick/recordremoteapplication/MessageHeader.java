package com.patrick.recordremoteapplication;

import java.net.InetAddress;

/**
 * Created by pat on 12/28/2014.
 */
public class MessageHeader {

    public MessageHeader() {

    }

    public MessageHeader(InetAddress sourceAddress, InetAddress destinationAddress, MessageCommand command) {
        SourceAddress = sourceAddress;
        DestinationAddress = destinationAddress;
        Command = command;
    }

    public InetAddress SourceAddress;
    public InetAddress DestinationAddress;
    public MessageCommand Command;

}
