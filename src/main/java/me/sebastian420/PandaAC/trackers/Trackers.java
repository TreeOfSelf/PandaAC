package me.sebastian420.PandaAC.trackers;

public class Trackers {
    private Trackers() { }

    public static final PlayerLastTeleportTracker PLAYER_LAST_TELEPORT_TRACKER = registerTracker(new PlayerLastTeleportTracker());

    public static <T extends Tracker<?>> T registerTracker(T tracker) {
        return tracker;
    }
}
