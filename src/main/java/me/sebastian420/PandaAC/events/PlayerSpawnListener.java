package me.sebastian420.PandaAC.events;

import me.sebastian420.PandaAC.objects.entity.PAPlayer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface PlayerSpawnListener {
    Event<PlayerSpawnListener> EVENT = EventFactory.createArrayBacked(PlayerSpawnListener.class,
        listeners -> player -> {
            for (PlayerSpawnListener listener : listeners) {
                listener.onSpawn(player);
            }
    });
    
    void onSpawn(PAPlayer player);
}
