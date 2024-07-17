package me.sebastian420.PandaAC.Objects;

import me.sebastian420.PandaAC.Objects.Data.PlayerMovementData;
import me.sebastian420.PandaAC.Util.MathUtil;
import me.sebastian420.PandaAC.View.PlayerMoveC2SPacketView;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;

public class MovementManager {
    public static void read(ServerPlayerEntity player, PlayerMoveC2SPacket packet) {
        long time = System.currentTimeMillis();
        PlayerMoveC2SPacketView packetView = (PlayerMoveC2SPacketView) packet;

        if (packetView.isChangePosition()) {
            PlayerMovementData playerData = PlayerMovementDataManager.getPlayer(player);

            long timeDifMs = time - playerData.getLastShortCheck();
            if (timeDifMs > 20 ) {
                double distance = MathUtil.getDistance(packetView.getX(), packetView.getZ(), playerData.getX(), playerData.getZ());
                double speedMps = (distance * 1000.0) / timeDifMs;
                playerData.setSpeedTime(speedMps);
            }

            playerData.setNew(packetView, time);
        }
    }
}
