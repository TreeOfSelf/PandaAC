package me.sebastian420.PandaAC.events;

import me.sebastian420.PandaAC.objects.PlayerInteractEntityC2SPacketView;
import me.sebastian420.PandaAC.objects.PlayerInteractEntityC2SPacketView.InteractType;
import me.sebastian420.PandaAC.objects.entity.PAEntity;
import me.sebastian420.PandaAC.objects.entity.PAPlayer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.Nullable;

public interface PlayerAttackListener {
    Event<PlayerAttackListener> EVENT = EventFactory.createArrayBacked(PlayerAttackListener.class,
        listeners -> (player, target, attackPacket) -> {
            for (PlayerAttackListener listener : listeners) {
                listener.onAttack(player, target, attackPacket);
            }
    });

    public static void init() {
        PacketCallback.EVENT.register((player, packet) -> {
            if (packet instanceof PlayerInteractEntityC2SPacketView playerInteractEntityC2SPacket) {
                PlayerInteractEntityC2SPacket rawPacket = (PlayerInteractEntityC2SPacket) packet;
                if (playerInteractEntityC2SPacket.getType() == InteractType.ATTACK) {
                    EVENT.invoker().onAttack(player, PAEntity.of(rawPacket.getEntity(player.getWorld())), playerInteractEntityC2SPacket);
                }
            }
            return ActionResult.PASS;
        });
    }

    void onAttack(PAPlayer player, @Nullable PAEntity target, PlayerInteractEntityC2SPacketView attackPacket);
}
