package com.patrick.recordremoteapplication;

/**
 * Created by pat on 2/25/2015.
 */
public enum BusyStatus {
    Unknown(0),
    Ready(1),
    Play(2),
    GoToTrack(3),
    Pause(4),
    Stop(5),
    Scan(6);

    public static BusyStatus fromInteger(int x) {
        switch (x) {
            case 0:
                return Unknown;
            case 1:
                return Ready;
            case 2:
                return Play;
            case 3:
                return GoToTrack;
            case 4:
                return Pause;
            case 5:
                return Stop;
            case 6:
                return Scan;
        }
        return null;
    }

    public String getString() {
        switch (this) {
            case Unknown:
                return "Unknown";
            case Pause:
                return "Pausing";
            case Play:
                return "Playing";
            case GoToTrack:
                return "Going to Track";
            case Stop:
                return "Stopping";
            case Scan:
                return "Scanning";
            case Ready:
                return "Ready";
            default:
                return "Unknown";
        }
    }

    public byte getValue() {
        return (byte) value;
    }

    private int value;

    private BusyStatus(int value) {
        this.value = value;
    }
}
