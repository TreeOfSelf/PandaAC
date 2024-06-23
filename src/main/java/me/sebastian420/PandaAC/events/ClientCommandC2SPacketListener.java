package me.sebastian420.PandaAC.events;

import me.sebastian420.PandaAC.objects.entity.PAPlayer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.ActionResult;

public interface ClientCommandC2SPacketListener {
    Event<ClientCommandC2SPacketListener> EVENT = EventFactory.createArrayBacked(ClientCommandC2SPacketListener.class,
        listeners -> (player, packet) -> {
            for (ClientCommandC2SPacketListener listener : listeners) {
                listener.onClientCommandC2SPacket(player, packet);
            }
    });

    public static void init() {
        PacketCallback.EVENT.register((player, packet) -> {
                if (packet instanceof ClientCommandC2SPacket) EVENT.invoker().onClientCommandC2SPacket(player,  (ClientCommandC2SPacket)packet);
                return ActionResult.PASS;
            }
        );
    }

    void onClientCommandC2SPacket(PAPlayer player, ClientCommandC2SPacket packet);
}
