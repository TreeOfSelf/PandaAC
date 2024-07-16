package me.sebastian420.PandaAC.events;

import me.sebastian420.PandaAC.objects.PlayerMoveC2SPacketView;
import me.sebastian420.PandaAC.objects.entity.PAPlayer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface PlayerMovementListener {
    Event<PlayerMovementListener> EVENT = EventFactory.createArrayBacked(PlayerMovementListener.class,
        listeners -> (player, packet, cause) -> {
            for (PlayerMovementListener listener : listeners) {
                listener.onMovement(player, packet, cause);
            }
    });

    public enum MoveCause {
        TELEPORT,
        OTHER;

        public boolean isTeleport() {
            return this == TELEPORT;
        }
    }

    void onMovement(PAPlayer player, PlayerMoveC2SPacketView packet, MoveCause cause);
}
