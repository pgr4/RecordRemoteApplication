package com.patrick.recordremoteapplication;

/**
 * Created by pat on 12/28/2014.
 */
public enum MessageCommand {
    None(0),
    NewAlbum(1),
    CurrentAlbum(2),
    Status(3),

    GoToTrack(10),
    Play(11),
    Pause(12),
    StopPause(13),

    Busy(20),
    Ready(21);

    public int getValue() {
        return value;
    }

    private int value;

    private MessageCommand(int value) {
        this.value = value;
    }
}



