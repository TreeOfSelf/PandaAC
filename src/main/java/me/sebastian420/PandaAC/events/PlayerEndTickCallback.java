package me.sebastian420.PandaAC.events;

import me.sebastian420.PandaAC.objects.entity.PAPlayer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerEndTickCallback {
    Event<PlayerEndTickCallback> EVENT = EventFactory.createArrayBacked(PlayerEndTickCallback.class,
        listeners -> player -> {
            for (PlayerEndTickCallback listener : listeners) {
                listener.onPlayerEndTick(player);
            }
    });

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                PlayerEndTickCallback.EVENT.invoker().onPlayerEndTick(PAPlayer.of(player));
            }
        });
    }

    void onPlayerEndTick(PAPlayer player);
}
