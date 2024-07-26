package me.sebastian420.PandaAC.check.player.elytra;

import me.sebastian420.PandaAC.manager.CheckManager;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import me.sebastian420.PandaAC.util.MathUtil;
import me.sebastian420.PandaAC.util.PandaLogger;
import net.minecraft.server.network.ServerPlayerEntity;

public class ElytraVerticalSpeedCheck {
    public static boolean check(ServerPlayerEntity serverPlayerEntity, PlayerMovementData playerData, long time) {
        //If the player has since had a movement packet
        boolean flagged = false;

        if (playerData.getChanged()) {

            long timeDifMs = time - playerData.getLastCheck();
            double distance = MathUtil.getDistance(
                    playerData.getLastY(),
                    playerData.getY());

            double speedMps = (distance * 1000.0) / timeDifMs;
            double speedPotential = playerData.getVerticalSpeedPotential((double) timeDifMs / 1000d);

            if (speedMps > speedPotential) {
                playerData.incrementUpSpeedFlagCount();
                if (playerData.getUpSpeedFlagCount() > 1) {
                    PandaLogger.getLogger().warn("Elytra Vertical Speed {} Potential {}", speedMps, speedPotential);
                    CheckManager.rollBack(serverPlayerEntity, playerData);
                    playerData.setCarriedPotential(0);
                } else {
                    playerData.decrementUpSpeedFlagCount();
                }
            }

        }
        return flagged;
    }
}
