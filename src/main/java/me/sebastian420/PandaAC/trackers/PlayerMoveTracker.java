package me.sebastian420.PandaAC.trackers;

import me.sebastian420.PandaAC.events.PacketCallback;
import me.sebastian420.PandaAC.events.PlayerMovementListener;
import me.sebastian420.PandaAC.events.PlayerMovementListener.MoveCause;
import me.sebastian420.PandaAC.events.TeleportConfirmListener;
import me.sebastian420.PandaAC.mixin.ServerPlayNetworkHandlerAccessor;
import me.sebastian420.PandaAC.objects.PlayerMoveC2SPacketView;
import me.sebastian420.PandaAC.objects.entity.PAEntity;
import me.sebastian420.PandaAC.objects.entity.PAPlayer;
import me.sebastian420.PandaAC.trackers.data.PlayerMoveData;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.NotNull;

public class PlayerMoveTracker extends Tracker<PlayerMoveData> implements PacketCallback {

    protected PlayerMoveTracker() {
        super(PlayerMoveData.class);
        PacketCallback.EVENT.register(this);
    }

    @Override
    public @NotNull PlayerMoveData get(PAEntity entity) {
        return entity.getOrCreateData(PlayerMoveData.class, PlayerMoveData::new);
    }

    public ActionResult onPacket(PAPlayer player, Packet<ServerPlayPacketListener> packet) {
        PlayerMoveData teleportConfirmData = get(player);

        if (packet instanceof TeleportConfirmC2SPacket) {
            teleportConfirmData.teleportConfirmC2SPacket = (TeleportConfirmC2SPacket)packet;

            if (teleportConfirmData.lastWasTeleportConfirm) {
                //player.kick(Text.literal("Illegal TeleportConfirmC2SPacket"));
            }

            checkTeleportConfirmId(player, teleportConfirmData, teleportConfirmData.teleportConfirmC2SPacket.getTeleportId());
            teleportConfirmData.lastWasTeleportConfirm = true;
            return ActionResult.PASS;
        }

        if (packet instanceof PlayerMoveC2SPacketView playerMoveC2SPacketView) {

            if (teleportConfirmData.lastWasTeleportConfirm) {
                teleportConfirmData.lastWasTeleportConfirm = false;

                if (playerMoveC2SPacketView.isChangePosition() && playerMoveC2SPacketView.isChangeLook()) {
                    TeleportConfirmListener.EVENT.invoker().onTeleportConfirm(player, teleportConfirmData.teleportConfirmC2SPacket, playerMoveC2SPacketView);
                    PlayerMovementListener.EVENT.invoker().onMovement(player, playerMoveC2SPacketView, MoveCause.TELEPORT);
                    player.setPacketPos(playerMoveC2SPacketView);
                } else {
                    //player.kick(Text.literal("Expected PlayerMoveC2SPacket.Both After TeleportConfirmC2SPacket"));
                }

                return ActionResult.PASS;
            }

            PlayerMovementListener.EVENT.invoker().onMovement(player, playerMoveC2SPacketView, MoveCause.OTHER);
            player.setPacketPos(playerMoveC2SPacketView);
        } else if (teleportConfirmData.lastWasTeleportConfirm) {
            //player.kick(Text.literal("Expected PlayerMoveC2SPacket After TeleportConfirmC2SPacket"));
        }

        teleportConfirmData.lastWasTeleportConfirm = false;
        return ActionResult.PASS;
    }
    
    private void checkTeleportConfirmId(PAPlayer player, PlayerMoveData data, int id) {
        int max = ((ServerPlayNetworkHandlerAccessor)player.getNetworkHandler()).getRequestedTeleportId();

        if (++data.expectedTeleportId == Integer.MAX_VALUE) {
            data.expectedTeleportId = 0;
        }
    }
}
