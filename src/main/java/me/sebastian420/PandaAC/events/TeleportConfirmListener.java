package me.sebastian420.PandaAC.events;

import me.sebastian420.PandaAC.objects.PlayerMoveC2SPacketView;
import me.sebastian420.PandaAC.objects.entity.PAPlayer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;

public interface TeleportConfirmListener {
    Event<TeleportConfirmListener> EVENT = EventFactory.createArrayBacked(TeleportConfirmListener.class,
        listeners -> (player, teleportConfirmC2SPacket, playerMoveC2SPacketView) -> {
            for (TeleportConfirmListener listener : listeners) {
                listener.onTeleportConfirm(player, teleportConfirmC2SPacket, playerMoveC2SPacketView);
            }
    });
    
    void onTeleportConfirm(PAPlayer player, TeleportConfirmC2SPacket teleportConfirmC2SPacket, PlayerMoveC2SPacketView playerMoveC2SPacketView);
}
