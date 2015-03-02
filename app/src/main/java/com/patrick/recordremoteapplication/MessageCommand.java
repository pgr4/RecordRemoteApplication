package com.patrick.recordremoteapplication;

/**
 * Created by pat on 12/28/2014.
 */
public enum MessageCommand {
    None(0),
    NewAlbum(1),
    CurrentAlbum(2),
    Status(3),
    Scan(4),
    Sync(5),

    GoToTrack(10),
    Play(11),
    Pause(12),
    StopPause(13),

    sUnknown(20),
    sReady(21),
    sPlay(22),
    sGoToTrack(23),
    sPause(24),
    sStop(25),
    sScan(26);

    public int getValue() {
        return value;
    }

    private int value;

    private MessageCommand(int value) {
        this.value = value;
    }
}



