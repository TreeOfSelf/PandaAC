package me.sebastian420.PandaAC.events;

import me.sebastian420.PandaAC.objects.entity.PAPlayer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.util.ActionResult;

public interface ClickSlotC2SPacketCallback {
    Event<ClickSlotC2SPacketCallback> EVENT = EventFactory.createArrayBacked(ClickSlotC2SPacketCallback.class,
        listeners -> (player, packet) -> {
            for (ClickSlotC2SPacketCallback listener : listeners) {
                ActionResult result = listener.onClickSlotC2SPacket(player, packet);

                if(result != ActionResult.PASS) {
                    return result;
                }
            }
        return ActionResult.PASS;
    });

    public static void init() {
        PacketCallback.EVENT.register((player, packet) -> 
            packet instanceof ClickSlotC2SPacket ? ClickSlotC2SPacketCallback.EVENT.invoker().onClickSlotC2SPacket(player, (ClickSlotC2SPacket)packet) : ActionResult.PASS
        );
    }

    ActionResult onClickSlotC2SPacket(PAPlayer player, ClickSlotC2SPacket packet);
}
