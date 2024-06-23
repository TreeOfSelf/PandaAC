package me.sebastian420.PandaAC.events;

import me.sebastian420.PandaAC.objects.entity.PAPlayer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public interface OutgoingTeleportListener {
    Event<OutgoingTeleportListener> EVENT = EventFactory.createArrayBacked(OutgoingTeleportListener.class,
        listeners -> (player, packet) -> {
            for (OutgoingTeleportListener listener : listeners) {
                listener.onOutgoingTeleport(player, packet);
            }
    });

    public static void init() {
        OutgoingPacketListener.EVENT.register((player, packet) -> {
            if (packet instanceof PlayerPositionLookS2CPacket) {
                OutgoingTeleportListener.EVENT.invoker().onOutgoingTeleport(player, (PlayerPositionLookS2CPacket) packet);
            }
        });
    }

    void onOutgoingTeleport(PAPlayer player, PlayerPositionLookS2CPacket packet);
}
