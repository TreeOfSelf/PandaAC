package me.sebastian420.PandaAC.check;

import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import me.sebastian420.PandaAC.util.MathUtil;
import me.sebastian420.PandaAC.util.PandaLogger;
import net.minecraft.server.network.ServerPlayerEntity;

public class HorizontalSpeedCheck {
    public static boolean check(ServerPlayerEntity serverPlayerEntity, PlayerMovementData playerData, long time) {
        //If the player has since had a movement packet
        boolean flagged = false;

        if (playerData.getChanged()) {

            long timeDifMs = time - playerData.getLastCheck();
            double distance = MathUtil.getDistance(playerData.getLastX(), playerData.getLastZ(), playerData.getX(), playerData.getZ());
            double speedMps = (distance * 1000.0) / timeDifMs;

            if (playerData.getPacketCount() <= 6) {
                playerData.setPossibleTimer(false);
            }

            double speedPotential = playerData.getSpeedPotential((double) timeDifMs / 1000d);
            double totalPotential = speedPotential + playerData.getCarriedPotential();

            if (speedMps > totalPotential || playerData.getPossibleTimer()) {
                serverPlayerEntity.teleport(serverPlayerEntity.getServerWorld(), playerData.getLastX(), playerData.getLastY(), playerData.getLastZ(), serverPlayerEntity.getYaw(), serverPlayerEntity.getPitch());
                playerData.teleport(playerData.getLastX(), playerData.getLastY(), playerData.getLastZ());
                playerData.setCarriedPotential(0);
                flagged = true;
            } else {
                playerData.setCarriedPotential(speedPotential - speedMps);
            }

            if (playerData.getPacketCount() > 6) {
                playerData.setPossibleTimer(true);
            }

        }
        return flagged;
    }
}