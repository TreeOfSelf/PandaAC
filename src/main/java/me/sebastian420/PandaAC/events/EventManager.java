package me.sebastian420.PandaAC.events;

public class EventManager {
    private EventManager() { }

    public static void init() {
        PlayerEndTickCallback.init();
        OutgoingTeleportListener.init();
        ClickSlotC2SPacketCallback.init();
        ClientCommandC2SPacketListener.init();
        InteractItemListener.init();
        PlayerRotationListener.init();
        PlayerAttackListener.init();
    }
}
