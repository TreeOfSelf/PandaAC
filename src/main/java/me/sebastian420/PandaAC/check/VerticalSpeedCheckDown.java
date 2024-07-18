package me.sebastian420.PandaAC.check;

import me.sebastian420.PandaAC.manager.CheckManager;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import me.sebastian420.PandaAC.util.MathUtil;
import me.sebastian420.PandaAC.util.PandaLogger;
import net.minecraft.server.network.ServerPlayerEntity;

public class VerticalSpeedCheckDown {
    public static boolean check(ServerPlayerEntity serverPlayerEntity, PlayerMovementData playerData, long time) {
        boolean flagged = false;

        //Make this based on the last time they had an attachment

        //If we have moved down since last
        if (playerData.getChanged() && playerData.getY() < playerData.getLastY()
                //If we are not currently attached (falling)
                && playerData.getLastAttachedY() != playerData.getY()) {


            long timeDifMs = time - playerData.getLastCheck();

            double tickTime = (timeDifMs / 1000d) * 20;
            double calculatedVelocity = (0.98 * Math.floor(tickTime) - 1) * 3.92;

            double distance = MathUtil.getDistance(playerData.getLastY(), playerData.getY());
            double speedMps = (distance * 1000.0) / timeDifMs;


            PandaLogger.getLogger().info("Speed down {} calculated {}", speedMps, calculatedVelocity);

            if (speedMps < Math.abs(serverPlayerEntity.getVelocity().getY())) {
                CheckManager.rollBack(serverPlayerEntity, playerData);
                flagged = true;
            }

        }
        return flagged;
    }
}
