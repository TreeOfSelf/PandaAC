package me.sebastian420.PandaAC.events;

import me.sebastian420.PandaAC.objects.entity.PAPlayer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.damage.DamageSource;

public interface PlayerDamageListener {
    Event<PlayerDamageListener> EVENT = EventFactory.createArrayBacked(PlayerDamageListener.class,
        listeners -> (player, source, amount) -> {
            for (PlayerDamageListener listener : listeners) {
                listener.onPlayerDamage(player, source, amount);
            }
    });

    void onPlayerDamage(PAPlayer player, DamageSource source, float amount);
}
