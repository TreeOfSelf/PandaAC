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
            double minVelocity = ((double) airTimeDif / 1000 * 4);
            
            long timeDifMs = time - playerData.getLastCheck();
            double distance = MathUtil.getDistance(playerData.getLastY(), playerData.getY());
            double speedMps = (distance * 1000.0) / timeDifMs;

            long solidBlockTimeDif = time - playerData.getLastSolidTouch();
            
            if ( (solidBlockTimeDif > 1000 && speedMps < minVelocity && airTimeDif > 500)) {
                CheckManager.rollBack(serverPlayerEntity, playerData);
                flagged = true;
            }

        }
        return flagged;
    }
}
