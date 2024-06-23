package me.sebastian420.PandaAC.events;

import me.sebastian420.PandaAC.objects.entity.PAPlayer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;

public interface OutgoingPacketListener {
    Event<OutgoingPacketListener> EVENT = EventFactory.createArrayBacked(OutgoingPacketListener.class,
        listeners -> (player, packet) -> {
            for (OutgoingPacketListener listener : listeners) {
                listener.onOutgoingPacket(player, packet);
            }
    });

    public void onOutgoingPacket(PAPlayer player, Packet<ClientPlayPacketListener> packet);
}
