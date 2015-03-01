package com.patrick.recordremoteapplication;

/**
 * Created by pat on 2/25/2015.
 */
public enum BusyStatus {
    Unknown(0),
    Play(1),
    GoToTrack(2),
    Pause(3),
    Stop(4);


    public static BusyStatus fromInteger(int x) {
        switch(x) {
            case 0:
                return Unknown;
            case 1:
                return Play;
            case 2:
                return GoToTrack;
            case 3:
                return Pause;
            case 4:
                return Stop;
        }
        return null;
    }

    public byte getValue() {
        return (byte)value;
    }

    private int value;

    private BusyStatus(int value) {
        this.value = value;
    }
}
