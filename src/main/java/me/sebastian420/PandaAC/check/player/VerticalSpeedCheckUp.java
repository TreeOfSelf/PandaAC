package me.sebastian420.PandaAC.check.player;

import me.sebastian420.PandaAC.manager.CheckManager;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import me.sebastian420.PandaAC.util.MathUtil;
import me.sebastian420.PandaAC.util.PandaLogger;
import net.minecraft.server.network.ServerPlayerEntity;

public class VerticalSpeedCheckUp {
    public static boolean check(ServerPlayerEntity serverPlayerEntity, PlayerMovementData playerData, long time) {
        boolean flagged = false;

        if (playerData.getChanged() && playerData.getY() > playerData.getLastY()) {
            //If we have moved up since last
            if (time - playerData.getLastFluidTime() > 500) {

                long timeDifMs = time - playerData.getLastCheck();
                double distance = MathUtil.getDistance(playerData.getLastY(), playerData.getY());
                double speedMps = (distance * 1000.0) / timeDifMs;

                double storedSpeed = playerData.getStoredSpeedVertical();

                double speedPotential = playerData.getVerticalSpeedPotential((double) timeDifMs / 1000d) + storedSpeed;

                double newStoredSpeed = storedSpeed - speedMps;

                if (newStoredSpeed > 0) {
                    playerData.setStoredSpeedVertical(newStoredSpeed);
                } else {
                    playerData.setStoredSpeedVertical(0);
                }

                if (speedMps > speedPotential) {
                    playerData.incrementUpSpeedFlagCount();
                    if (playerData.getUpSpeedFlagCount() > 1) {
                        PandaLogger.getLogger().warn("Speed {} Potential {} {}", speedMps, speedPotential, serverPlayerEntity.getPlayerListName());
                        CheckManager.rollBack(serverPlayerEntity, playerData);
                        flagged = true;
                    }
                } else {
                 playerData.decrementUpSpeedFlagCount();
                }
            }
        }
        return flagged;
    }
}
