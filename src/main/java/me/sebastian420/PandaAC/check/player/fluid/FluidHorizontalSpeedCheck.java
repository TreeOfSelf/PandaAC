package me.sebastian420.PandaAC.check.player.fluid;

import me.sebastian420.PandaAC.manager.CheckManager;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import me.sebastian420.PandaAC.util.MathUtil;
import me.sebastian420.PandaAC.util.PandaLogger;
import net.minecraft.server.network.ServerPlayerEntity;

public class FluidHorizontalSpeedCheck {
    public static boolean check(ServerPlayerEntity serverPlayerEntity, PlayerMovementData playerData, long time) {
        //If the player has since had a movement packet
        boolean flagged = false;

        if (playerData.getChanged()) {

            long timeDifMs = time - playerData.getLastCheck();
            double distance = MathUtil.getDistance(
                    playerData.getLastX(), playerData.getLastZ(),
                    playerData.getX(), playerData.getZ());

            double speedMps = (distance * 1000.0) / timeDifMs;

            if (playerData.getPacketCount() <= 6) {
                playerData.setPossibleTimer(false);
            }

            double storedSpeed = playerData.getStoredSpeed();

            double speedPotential = playerData.getSpeedPotential((double) timeDifMs / 1000d);
            double totalPotential = speedPotential + storedSpeed;

            double newStoredSpeed = storedSpeed - speedMps;

            if (newStoredSpeed > 0) {
                playerData.setStoredSpeed(newStoredSpeed);
            } else {
                playerData.setStoredSpeed(0);
            }


            if (speedMps > totalPotential || playerData.getPossibleTimer()) {

                PandaLogger.getLogger().warn("Swim Horizontal Speed {} Potential {} Count {} {}", speedMps, totalPotential, playerData.getPacketCount(), serverPlayerEntity.getPlayerListName());
                CheckManager.rollBack(serverPlayerEntity, playerData);
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
