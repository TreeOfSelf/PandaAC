package me.sebastian420.PandaAC.events;

import me.sebastian420.PandaAC.objects.entity.PAEntity;
import me.sebastian420.PandaAC.objects.entity.PAPlayer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface PlayerStartRidingListener {
    Event<PlayerStartRidingListener> EVENT = EventFactory.createArrayBacked(PlayerStartRidingListener.class,
        listeners -> (player, vehicle) -> {
            for (PlayerStartRidingListener listener : listeners) {
                listener.onStartRiding(player, vehicle);
            }
    });

    void onStartRiding(PAPlayer player, PAEntity vehicle);
}
