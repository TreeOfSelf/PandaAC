package me.sebastian420.PandaAC.events;

import me.sebastian420.PandaAC.objects.entity.PAPlayer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.ActionResult;

public interface PacketCallback {
    Event<PacketCallback> EVENT = EventFactory.createArrayBacked(PacketCallback.class,
        listeners -> (player, packet) -> {
            for (PacketCallback listener : listeners) {
                ActionResult result = listener.onPacket(player, packet);

                if(result != ActionResult.PASS) {
                    return result;
                }
            }

        return ActionResult.PASS;
    });

    ActionResult onPacket(PAPlayer player, Packet<ServerPlayPacketListener> packet);
}
