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



            long airTimeDif = time - playerData.getAirTimeStartTime();
            double tickTime = (airTimeDif / 1000d) * 20;
            double calculatedVelocity = (0.98 * Math.floor(tickTime) - 1) * 0.5;

            long timeDifMs = time - playerData.getLastCheck();
            double distance = MathUtil.getDistance(playerData.getLastY(), playerData.getY());
            double speedMps = (distance * 1000.0) / timeDifMs;


            PandaLogger.getLogger().info("Speed down {} calculated {} Time dif {}", speedMps, calculatedVelocity, airTimeDif);

            if (speedMps < calculatedVelocity) {
                CheckManager.rollBack(serverPlayerEntity, playerData);
                flagged = true;
            }

        }
        return flagged;
    }
}
