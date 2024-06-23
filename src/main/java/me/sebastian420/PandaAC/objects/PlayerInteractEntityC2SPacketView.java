package me.sebastian420.PandaAC.objects;

public interface PlayerInteractEntityC2SPacketView {
    InteractType getType();

    enum InteractType {
        INTERACT,
        ATTACK,
        INTERACT_AT;

        public static final InteractType[] ALL = InteractType.values(); 
    }
}
